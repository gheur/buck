/*
 * Copyright 2016-present Facebook, Inc.
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
import com.facebook.buck.command.BuildExecutorArgs;
import com.facebook.buck.command.LocalBuildExecutor;
import com.facebook.buck.distributed.thrift.BuildJob;
import com.facebook.buck.log.Logger;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.parser.BuildTargetParser;
import com.facebook.buck.rules.CellPathResolver;
import com.facebook.buck.step.ExecutionContext;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DistBuildSlaveExecutor {

  private static final Logger LOG = Logger.get(DistBuildSlaveExecutor.class);
  private static final String LOCALHOST_ADDRESS = "localhost";
  private static final boolean KEEP_GOING = true;

  private final DistBuildSlaveExecutorArgs args;
  private final DelegateAndGraphsInitializer initializer;

  public DistBuildSlaveExecutor(DistBuildSlaveExecutorArgs args) {
    this.args = args;
    this.initializer =
        new DelegateAndGraphsInitializer(args.createDelegateAndGraphsInitiazerArgs());
  }

  public int buildAndReturnExitCode() throws IOException, InterruptedException {
    if (DistBuildMode.COORDINATOR == args.getDistBuildMode()) {
      return newCoordinatorMode(false).runAndReturnExitCode();
    }

    DistBuildModeRunner runner = null;
    BuildExecutorArgs builderArgs = args.createBuilderArgs();
    try (ExecutionContext executionContext =
        LocalBuildExecutor.createExecutionContext(builderArgs)) {
      BuildExecutor localBuildExecutor = createBuilder(builderArgs, executionContext);

      switch (args.getDistBuildMode()) {
        case REMOTE_BUILD:
          runner =
              new RemoteBuildModeRunner(
                  localBuildExecutor,
                  args.getState().getRemoteState().getTopLevelTargets(),
                  exitCode ->
                      args.getDistBuildService()
                          .setFinalBuildStatus(
                              args.getStampedeId(),
                              BuildStatusUtil.exitCodeToBuildStatus(exitCode)));
          break;

        case MINION:
          runner =
              newMinionMode(
                  localBuildExecutor,
                  args.getRemoteCoordinatorAddress(),
                  OptionalInt.of(args.getRemoteCoordinatorPort()));
          break;

        case COORDINATOR_AND_MINION:
          runner =
              new CoordinatorAndMinionModeRunner(
                  newCoordinatorMode(true),
                  newMinionMode(localBuildExecutor, LOCALHOST_ADDRESS, OptionalInt.empty()));
          break;

        case COORDINATOR:
          throw new IllegalStateException("COORDINATOR mode should have already been handled.");

        default:
          LOG.error("Unknown distributed build mode [%s].", args.getDistBuildMode().toString());
          return -1;
      }
    }
    return runner.runAndReturnExitCode();
  }

  private BuildExecutor createBuilder(
      BuildExecutorArgs builderArgs, ExecutionContext executionContext) {
    Supplier<BuildExecutor> builderSupplier =
        () -> {
          DelegateAndGraphs delegateAndGraphs = null;
          try {
            delegateAndGraphs = initializer.getDelegateAndGraphs().get();
          } catch (InterruptedException | ExecutionException e) {
            String msg = String.format("Failed to get the DelegateAndGraphs.");
            LOG.error(e, msg);
            throw new RuntimeException(msg, e);
          }
          return new LocalBuildExecutor(
              builderArgs,
              executionContext,
              delegateAndGraphs.getActionGraphAndResolver(),
              delegateAndGraphs.getCachingBuildEngineDelegate(),
              args.getArtifactCache(),
              args.getExecutorService(),
              KEEP_GOING,
              Optional.empty(),
              Optional.empty(),
              Optional.empty());
        };
    return new LazyInitBuilder(builderSupplier);
  }

  private MinionModeRunner newMinionMode(
      BuildExecutor localBuildExecutor, String coordinatorAddress, OptionalInt coordinatorPort) {
    MinionModeRunner.BuildCompletionChecker checker =
        () -> {
          BuildJob job = args.getDistBuildService().getCurrentBuildJobState(args.getStampedeId());
          return BuildStatusUtil.isTerminalBuildStatus(job.getStatus());
        };

    return new MinionModeRunner(
        coordinatorAddress,
        coordinatorPort,
        localBuildExecutor,
        args.getStampedeId(),
        args.getBuildSlaveRunId(),
        args.getBuildThreadCount(),
        checker);
  }

  private CoordinatorModeRunner newCoordinatorMode(boolean isLocalMinionAlsoRunning) {
    ListenableFuture<BuildTargetsQueue> queue =
        Futures.transform(initializer.getDelegateAndGraphs(), x -> createBuildQueue(x));
    Optional<String> minionQueue = args.getDistBuildConfig().getMinionQueue();
    Preconditions.checkArgument(
        minionQueue.isPresent(),
        "Minion queue name is missing to be able to run in Coordinator mode.");
    ThriftCoordinatorServer.EventListener listener =
        new CoordinatorEventListener(
            args.getDistBuildService(),
            args.getStampedeId(),
            minionQueue.get(),
            isLocalMinionAlsoRunning);
    return new CoordinatorModeRunner(queue, args.getStampedeId(), listener);
  }

  private BuildTargetsQueue createBuildQueue(DelegateAndGraphs delegateAndGraphs) {
    final CellPathResolver cellNames = args.getState().getRootCell().getCellPathResolver();
    List<BuildTarget> targets =
        args.getState()
            .getRemoteState()
            .getTopLevelTargets()
            .stream()
            .map(target -> BuildTargetParser.fullyQualifiedNameToBuildTarget(cellNames, target))
            .collect(Collectors.toList());
    BuildTargetsQueue queue =
        BuildTargetsQueue.newQueue(
            delegateAndGraphs.getActionGraphAndResolver().getResolver(), targets);
    return queue;
  }
}
