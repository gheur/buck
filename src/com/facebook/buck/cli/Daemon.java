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

package com.facebook.buck.cli;

import com.facebook.buck.artifact_cache.ArtifactCache;
import com.facebook.buck.artifact_cache.ArtifactCaches;
import com.facebook.buck.artifact_cache.config.ArtifactCacheBuckConfig;
import com.facebook.buck.core.cell.Cell;
import com.facebook.buck.core.config.BuckConfig;
import com.facebook.buck.core.model.actiongraph.computation.ActionGraphCache;
import com.facebook.buck.core.rulekey.RuleKey;
import com.facebook.buck.core.rules.knowntypes.KnownRuleTypesProvider;
import com.facebook.buck.event.BuckEventBus;
import com.facebook.buck.event.FileHashCacheEvent;
import com.facebook.buck.httpserver.WebServer;
import com.facebook.buck.io.ExecutableFinder;
import com.facebook.buck.io.filesystem.ProjectFilesystem;
import com.facebook.buck.io.watchman.WatchmanCursor;
import com.facebook.buck.io.watchman.WatchmanWatcher;
import com.facebook.buck.log.Logger;
import com.facebook.buck.parser.DefaultParser;
import com.facebook.buck.parser.Parser;
import com.facebook.buck.parser.ParserConfig;
import com.facebook.buck.parser.ParserPythonInterpreterProvider;
import com.facebook.buck.parser.PerBuildStateFactory;
import com.facebook.buck.parser.TargetSpecResolver;
import com.facebook.buck.rules.coercer.ConstructorArgMarshaller;
import com.facebook.buck.rules.coercer.DefaultTypeCoercerFactory;
import com.facebook.buck.rules.coercer.TypeCoercerFactory;
import com.facebook.buck.rules.keys.DefaultRuleKeyCache;
import com.facebook.buck.rules.keys.RuleKeyCacheRecycler;
import com.facebook.buck.support.bgtasks.BackgroundTaskManager;
import com.facebook.buck.support.bgtasks.SynchronousBackgroundTaskManager;
import com.facebook.buck.util.RichStream;
import com.facebook.buck.util.cache.ProjectFileHashCache;
import com.facebook.buck.util.cache.impl.DefaultFileHashCache;
import com.facebook.buck.util.cache.impl.WatchedFileHashCache;
import com.facebook.buck.versions.VersionedTargetGraphCache;
import com.facebook.buck.worker.WorkerProcessPool;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Daemon used to monitor the file system and cache build rules between Main() method invocations is
 * static so that it can outlive Main() objects and survive for the lifetime of the potentially long
 * running Buck process.
 */
final class Daemon implements Closeable {
  private static final Logger LOG = Logger.get(Daemon.class);

  private final Cell rootCell;
  private final TypeCoercerFactory typeCoercerFactory;
  private final Parser parser;
  private final ImmutableList<ProjectFileHashCache> hashCaches;
  private final EventBus fileEventBus;
  private final Optional<WebServer> webServer;
  private final ConcurrentMap<String, WorkerProcessPool> persistentWorkerPools;
  private final VersionedTargetGraphCache versionedTargetGraphCache;
  private final ActionGraphCache actionGraphCache;
  private final RuleKeyCacheRecycler<RuleKey> defaultRuleKeyFactoryCacheRecycler;
  private final ImmutableMap<Path, WatchmanCursor> cursor;
  private final KnownRuleTypesProvider knownRuleTypesProvider;

  private final BackgroundTaskManager bgTaskManager;

  Daemon(
      Cell rootCell,
      KnownRuleTypesProvider knownRuleTypesProvider,
      ExecutableFinder executableFinder,
      Optional<WebServer> webServerToReuse) {
    this.rootCell = rootCell;
    this.fileEventBus = new EventBus("file-change-events");

    ImmutableList<Cell> allCells = rootCell.getAllCells();

    // Setup the stacked file hash cache from all cells.
    ImmutableList.Builder<ProjectFileHashCache> hashCachesBuilder = ImmutableList.builder();
    allCells.forEach(
        subCell -> {
          WatchedFileHashCache watchedCache =
              new WatchedFileHashCache(
                  subCell.getFilesystem(), rootCell.getBuckConfig().getFileHashCacheMode());
          fileEventBus.register(watchedCache);
          hashCachesBuilder.add(watchedCache);
        });
    hashCachesBuilder.add(
        DefaultFileHashCache.createBuckOutFileHashCache(
            rootCell.getFilesystem(), rootCell.getBuckConfig().getFileHashCacheMode()));
    this.hashCaches = hashCachesBuilder.build();

    this.actionGraphCache =
        new ActionGraphCache(rootCell.getBuckConfig().getMaxActionGraphCacheEntries());
    this.versionedTargetGraphCache = new VersionedTargetGraphCache();
    this.knownRuleTypesProvider = knownRuleTypesProvider;

    typeCoercerFactory = new DefaultTypeCoercerFactory();
    ParserConfig parserConfig = rootCell.getBuckConfig().getView(ParserConfig.class);
    this.parser =
        new DefaultParser(
            new PerBuildStateFactory(
                typeCoercerFactory,
                new ConstructorArgMarshaller(typeCoercerFactory),
                knownRuleTypesProvider,
                new ParserPythonInterpreterProvider(parserConfig, executableFinder)),
            parserConfig,
            typeCoercerFactory,
            new TargetSpecResolver());
    parser.register(fileEventBus);

    // Build the the rule key cache recycler.
    this.defaultRuleKeyFactoryCacheRecycler =
        RuleKeyCacheRecycler.createAndRegister(
            fileEventBus,
            new DefaultRuleKeyCache<>(),
            RichStream.from(allCells).map(Cell::getFilesystem).toImmutableSet());

    if (webServerToReuse.isPresent()) {
      webServer = webServerToReuse;
    } else {
      webServer = createWebServer(rootCell.getBuckConfig(), rootCell.getFilesystem());
    }
    if (!initWebServer()) {
      LOG.warn("Can't start web server");
    }
    if (rootCell.getBuckConfig().getView(ParserConfig.class).getWatchmanCursor()
            == WatchmanWatcher.CursorType.CLOCK_ID
        && !rootCell.getWatchman().getClockIds().isEmpty()) {
      cursor = rootCell.getWatchman().buildClockWatchmanCursorMap();
    } else {
      LOG.debug("Falling back to named cursors: %s", rootCell.getWatchman().getProjectWatches());
      cursor = rootCell.getWatchman().buildNamedWatchmanCursorMap();
    }
    LOG.debug("Using Watchman Cursor: %s", cursor);
    persistentWorkerPools = new ConcurrentHashMap<>();

    this.bgTaskManager = new SynchronousBackgroundTaskManager(true);
  }

  Cell getRootCell() {
    return rootCell;
  }

  private static Optional<WebServer> createWebServer(
      BuckConfig config, ProjectFilesystem filesystem) {
    OptionalInt port = getValidWebServerPort(config);
    if (!port.isPresent()) {
      return Optional.empty();
    }
    return Optional.of(new WebServer(port.getAsInt(), filesystem));
  }

  /**
   * If the return value is not absent, then the port is a nonnegative integer. This means that
   * specifying a port of -1 effectively disables the WebServer.
   */
  static OptionalInt getValidWebServerPort(BuckConfig config) {
    // Enable the web httpserver if it is given by command line parameter or specified in
    // .buckconfig. The presence of a nonnegative port number is sufficient.
    Optional<String> serverPort = Optional.ofNullable(System.getProperty("buck.httpserver.port"));
    if (!serverPort.isPresent()) {
      serverPort = config.getValue("httpserver", "port");
    }

    if (!serverPort.isPresent() || serverPort.get().isEmpty()) {
      return OptionalInt.empty();
    }

    String rawPort = serverPort.get();
    int port;
    try {
      port = Integer.parseInt(rawPort, 10);
    } catch (NumberFormatException e) {
      LOG.error("Could not parse port for httpserver: %s.", rawPort);
      return OptionalInt.empty();
    }

    return port >= 0 ? OptionalInt.of(port) : OptionalInt.empty();
  }

  BackgroundTaskManager getBgTaskManager() {
    return bgTaskManager;
  }

  Optional<WebServer> getWebServer() {
    return webServer;
  }

  TypeCoercerFactory getTypeCoercerFactory() {
    return typeCoercerFactory;
  }

  Parser getParser() {
    return parser;
  }

  VersionedTargetGraphCache getVersionedTargetGraphCache() {
    return versionedTargetGraphCache;
  }

  ActionGraphCache getActionGraphCache() {
    return actionGraphCache;
  }

  ImmutableList<ProjectFileHashCache> getFileHashCaches() {
    return hashCaches;
  }

  public KnownRuleTypesProvider getKnownRuleTypesProvider() {
    return knownRuleTypesProvider;
  }

  ConcurrentMap<String, WorkerProcessPool> getPersistentWorkerPools() {
    return persistentWorkerPools;
  }

  RuleKeyCacheRecycler<RuleKey> getDefaultRuleKeyFactoryCacheRecycler() {
    return defaultRuleKeyFactoryCacheRecycler;
  }

  void interruptOnClientExit(Thread threadToInterrupt) {
    // Synchronize on parser object so that the main command processing thread is not
    // interrupted mid way through a Parser cache update by the Thread.interrupt() call
    // triggered by System.exit(). The Parser cache will be reused by subsequent commands
    // so needs to be left in a consistent state even if the current command is interrupted
    // due to a client disconnection.
    synchronized (parser) {
      // signal to the main thread that we want to exit
      threadToInterrupt.interrupt();
    }
  }

  void watchFileSystem(
      BuckEventBus eventBus,
      WatchmanWatcher watchmanWatcher,
      WatchmanWatcher.FreshInstanceAction watchmanFreshInstanceAction)
      throws IOException, InterruptedException {

    // Synchronize on parser object so that all outstanding watch events are processed
    // as a single, atomic Parser cache update and are not interleaved with Parser cache
    // invalidations triggered by requests to parse build files or interrupted by client
    // disconnections.
    synchronized (parser) {
      // Track the file hash cache invalidation run time.
      FileHashCacheEvent.InvalidationStarted started = FileHashCacheEvent.invalidationStarted();
      eventBus.post(started);
      try {
        watchmanWatcher.postEvents(eventBus, watchmanFreshInstanceAction);
      } finally {
        eventBus.post(FileHashCacheEvent.invalidationFinished(started));
        hashCaches.forEach(
            hashCache -> {
              if (hashCache instanceof WatchedFileHashCache) {
                WatchedFileHashCache cache = (WatchedFileHashCache) hashCache;
                cache.getStatsEvents().forEach(eventBus::post);
              }
            });
      }
    }
  }

  /** @return true if the web server was started successfully. */
  private boolean initWebServer() {
    if (webServer.isPresent()) {
      Optional<ArtifactCache> servedCache =
          ArtifactCaches.newServedCache(
              new ArtifactCacheBuckConfig(rootCell.getBuckConfig()), rootCell.getFilesystem());
      try {
        webServer.get().updateAndStartIfNeeded(servedCache);
        return true;
      } catch (WebServer.WebServerException e) {
        LOG.error(e);
      }
    }
    return false;
  }

  public EventBus getFileEventBus() {
    return fileEventBus;
  }

  public ImmutableMap<Path, WatchmanCursor> getWatchmanCursor() {
    return cursor;
  }

  @Override
  public void close() {
    shutdownPersistentWorkerPools();
    shutdownWebServer();
  }

  private void shutdownPersistentWorkerPools() {
    for (WorkerProcessPool pool : persistentWorkerPools.values()) {
      try {
        pool.close();
      } catch (Exception e) {
        LOG.error(e);
      }
    }
  }

  private void shutdownWebServer() {
    if (webServer.isPresent()) {
      try {
        webServer.get().stop();
      } catch (WebServer.WebServerException e) {
        LOG.error(e);
      }
    }
  }
}
