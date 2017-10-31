/*
 * Copyright 2017-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.distributed;

import com.facebook.buck.command.BuildExecutor;
import com.facebook.buck.distributed.thrift.BuildSlaveRunId;
import com.facebook.buck.distributed.thrift.GetWorkResponse;
import com.facebook.buck.distributed.thrift.StampedeId;
import com.facebook.buck.log.CommandThreadFactory;
import com.facebook.buck.log.Logger;
import com.facebook.buck.rules.BuildEngineResult;
import com.facebook.buck.rules.BuildResult;
import com.facebook.buck.slb.ThriftException;
import com.facebook.buck.util.concurrent.MostExecutors;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public class MinionModeRunner implements DistBuildModeRunner {
  private static final Logger LOG = Logger.get(MinionModeRunner.class);

  // TODO(alisdair): make this a .buckconfig setting
  private static final int IDLE_SLEEP_INTERVAL_MS = 10;

  private final String coordinatorAddress;
  private volatile OptionalInt coordinatorPort;
  private final BuildExecutor buildExecutor;
  private final StampedeId stampedeId;
  private final BuildSlaveRunId buildSlaveRunId;

  private final BuildCompletionChecker buildCompletionChecker;
  private final ExecutorService buildExecutorService;

  private final MinionLocalBuildStateTracker buildTracker;

  // Signals to the main loop that it can stop requesting new work.
  private final AtomicBoolean finished = new AtomicBoolean(false);

  // Aggregate exit code for the minion. Non-zero if any set of build targets failed.
  private AtomicInteger exitCode = new AtomicInteger(0);

  /** Callback when the build has completed. */
  public interface BuildCompletionChecker {
    boolean hasBuildFinished() throws IOException;
  }

  /** Encapsulates a Thrift call */
  @FunctionalInterface
  public interface ThriftCall {
    void apply() throws IOException;
  }

  public MinionModeRunner(
      String coordinatorAddress,
      BuildExecutor buildExecutor,
      StampedeId stampedeId,
      BuildSlaveRunId buildSlaveRunId,
      int availableWorkUnitBuildCapacity,
      BuildCompletionChecker buildCompletionChecker) {
    this(
        coordinatorAddress,
        OptionalInt.empty(),
        buildExecutor,
        stampedeId,
        buildSlaveRunId,
        availableWorkUnitBuildCapacity,
        buildCompletionChecker);
  }

  public MinionModeRunner(
      String coordinatorAddress,
      OptionalInt coordinatorPort,
      BuildExecutor buildExecutor,
      StampedeId stampedeId,
      BuildSlaveRunId buildSlaveRunId,
      int availableWorkUnitBuildCapacity,
      BuildCompletionChecker buildCompletionChecker) {
    this(
        coordinatorAddress,
        coordinatorPort,
        buildExecutor,
        stampedeId,
        buildSlaveRunId,
        availableWorkUnitBuildCapacity,
        buildCompletionChecker,
        MostExecutors.newMultiThreadExecutor(
            new CommandThreadFactory("MinionBuilderThread"), availableWorkUnitBuildCapacity));
  }

  @VisibleForTesting
  public MinionModeRunner(
      String coordinatorAddress,
      OptionalInt coordinatorPort,
      BuildExecutor buildExecutor,
      StampedeId stampedeId,
      BuildSlaveRunId buildSlaveRunId,
      int maxWorkUnitBuildCapacity,
      BuildCompletionChecker buildCompletionChecker,
      ExecutorService buildExecutorService) {
    this.buildExecutor = buildExecutor;
    this.stampedeId = stampedeId;
    this.buildSlaveRunId = buildSlaveRunId;
    this.coordinatorAddress = coordinatorAddress;
    coordinatorPort.ifPresent(CoordinatorModeRunner::validatePort);
    this.coordinatorPort = coordinatorPort;

    this.buildCompletionChecker = buildCompletionChecker;
    this.buildExecutorService = buildExecutorService;
    this.buildTracker = new MinionLocalBuildStateTracker(maxWorkUnitBuildCapacity);

    LOG.info(
        String.format(
            "Started new minion that can build [%d] work units in parallel",
            maxWorkUnitBuildCapacity));
  }

  @Override
  public int runAndReturnExitCode() throws IOException, InterruptedException {
    Preconditions.checkState(coordinatorPort.isPresent(), "Coordinator port has not been set.");

    try (ThriftCoordinatorClient client =
        new ThriftCoordinatorClient(coordinatorAddress, stampedeId)) {
      completionCheckingThriftCall(() -> client.start(coordinatorPort.getAsInt()));

      final String minionId = generateMinionId(buildSlaveRunId);

      while (!finished.get()) {
        signalFinishedTargetsAndFetchMoreWork(minionId, client);
        Thread.sleep(IDLE_SLEEP_INTERVAL_MS);
      }

      completionCheckingThriftCall(() -> client.stop());
    }

    // At this point there is no more work to schedule, so wait for the build to finish.
    buildExecutorService.shutdown();
    buildExecutorService.awaitTermination(30, TimeUnit.MINUTES);

    buildExecutor.shutdown();

    return exitCode.get();
  }

  public void setCoordinatorPort(int coordinatorPort) {
    CoordinatorModeRunner.validatePort(coordinatorPort);
    this.coordinatorPort = OptionalInt.of(coordinatorPort);
  }

  private void signalFinishedTargetsAndFetchMoreWork(
      String minionId, ThriftCoordinatorClient client) throws IOException, InterruptedException {
    List<String> targetsToSignal = buildTracker.getTargetsToSignal();

    if (!buildTracker.capacityAvailable() && exitCode.get() == 0 && targetsToSignal.size() == 0) {
      return; // Making a request will not move the build forward, so wait a while and try again.
    }

    LOG.info(
        String.format(
            "Minion [%s] fetching work. Signalling [%d] finished targets",
            minionId, targetsToSignal.size()));

    try {
      GetWorkResponse response =
          client.getWork(
              minionId, exitCode.get(), targetsToSignal, buildTracker.getAvailableCapacity());
      if (!response.isContinueBuilding()) {
        LOG.info(String.format("Minion [%s] told to stop building.", minionId));
        finished.set(true);
      }

      buildTracker.enqueueWorkUnitsForBuilding(response.getWorkUnits());
    } catch (ThriftException ex) {
      handleThriftException(ex);
      return;
    }

    if (!buildTracker.outstandingWorkUnitsToBuild()) {
      return; // Nothing new to build
    }

    buildExecutorService.execute(
        () -> {
          try {
            performBuildOfWorkUnits(minionId);
          } catch (IOException e) {
            LOG.error(e, "Failed whilst building targets. Terminating build. ");
            exitCode.set(-1);
            finished.set(true);
          }
        });
  }

  private void performBuildOfWorkUnits(String minionId) throws IOException {
    List<String> targetsToBuild = buildTracker.getTargetsToBuild();

    if (targetsToBuild.size() == 0) {
      return; // All outstanding targets have already been picked up by an earlier build thread.
    }

    LOG.info(
        String.format(
            "Minion [%s] is about to build [%d] targets", minionId, targetsToBuild.size()));
    LOG.debug(String.format("Targets: [%s]", Joiner.on(", ").join(targetsToBuild)));

    // Start the build, and get futures representing the results.
    List<BuildEngineResult> resultFutures = buildExecutor.initializeBuild(targetsToBuild);

    // Register handlers that will ensure we free up cores as soon as a work unit is complete,
    // and signal built targets as soon as they are uploaded to the cache.
    for (BuildEngineResult resultFuture : resultFutures) {
      registerBuildRuleCompletionHandler(resultFuture);
    }

    // Wait for the targets to finish building and get the exit code.
    int lastExitCode =
        buildExecutor.waitForBuildToFinish(targetsToBuild, resultFutures, Optional.empty());

    LOG.info(String.format("Minion [%s] finished with exit code [%d].", minionId, lastExitCode));

    if (lastExitCode != 0) {
      exitCode.set(lastExitCode);
    }
  }

  private void registerBuildRuleCompletionHandler(BuildEngineResult resultFuture) {
    Futures.addCallback(
        resultFuture.getResult(),
        new FutureCallback<BuildResult>() {
          @Override
          public void onSuccess(@Nullable BuildResult result) {
            Preconditions.checkNotNull(result);

            final String fullyQualifiedName = result.getRule().getFullyQualifiedName();

            if (result.getSuccess() == null) {
              LOG.error(String.format("Building of target [%s] failed.", fullyQualifiedName));
              exitCode.set(1); // Ensure the build doesn't deadlock
              return;
            } else {
              LOG.info(String.format("Building of target [%s] completed.", fullyQualifiedName));
            }

            buildTracker.recordFinishedTarget(fullyQualifiedName);
            registerUploadCompletionHandler(Preconditions.checkNotNull(result));
          }

          @Override
          public void onFailure(Throwable t) {
            LOG.error(t, String.format("Building of unknown target failed."));
            exitCode.set(1); // Fail the Stampede build, and ensure it doesn't deadlock.
          }
        });
  }

  private void registerUploadCompletionHandler(final BuildResult buildResult) {
    final String fullyQualifiedName = buildResult.getRule().getFullyQualifiedName();
    Futures.addCallback(
        buildResult.getUploadCompleteFuture(),
        new FutureCallback<Void>() {
          @Override
          public void onSuccess(@Nullable Void result) {
            buildTracker.recordUploadedTarget(fullyQualifiedName);
          }

          @Override
          public void onFailure(Throwable t) {
            // TODO(alisdair,ruibm): re-try this, maybe on a different minion.
            LOG.error(t, String.format("Cache upload failed for target %s", fullyQualifiedName));
            exitCode.set(1); // Fail the Stampede build, and ensure it doesn't deadlock.
          }
        });
  }

  private void completionCheckingThriftCall(ThriftCall thriftCall) throws IOException {
    try {
      thriftCall.apply();
    } catch (ThriftException e) {
      handleThriftException(e);
      return;
    }
  }

  private void handleThriftException(ThriftException e) throws IOException {
    if (buildCompletionChecker.hasBuildFinished()) {
      // If the build has finished and this minion was not doing anything and was just
      // waiting for work, just exit gracefully with return code 0.
      LOG.warn(
          e,
          ("Minion failed to connect to coordinator, "
              + "but build already finished, so shutting down."));
      finished.set(true);
      return;
    } else {
      throw e;
    }
  }

  private static String generateMinionId(BuildSlaveRunId buildSlaveRunId) {
    Preconditions.checkState(!buildSlaveRunId.getId().isEmpty());

    String hostname = "Unknown";
    try {
      InetAddress addr;
      addr = InetAddress.getLocalHost();
      hostname = addr.getHostName();
    } catch (UnknownHostException ex) {
      System.out.println("Hostname can not be resolved");
    }

    return String.format("minion:%s:%s", hostname, buildSlaveRunId);
  }
}
