/*
 * This file is part of the MCDR-Completion project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  DancingSnow and contributors
 *
 * MCDR-Completion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MCDR-Completion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MCDR-Completion.  If not, see <https://www.gnu.org/licenses/>.
 */

package cn.dancingsnow.mcdrc.server;

import cn.dancingsnow.mcdrc.MCDRCompletion;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class NodeChangeWatcher extends Thread{
    public static final NodeChangeWatcher INSTANCE = new NodeChangeWatcher();

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setPriority(Thread.MIN_PRIORITY).setDaemon(true)
                    .setNameFormat("MCDRC Scheduler").build()
    );

    public static void init() {

    }

    private NodeChangeWatcher() {
        super("MCDRC Watcher Thread");
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
    }

    private final Path nodePath = Path.of(MCDRCompletionServer.modConfig.getNodePath());
    private WatchService watchService;

    @Override
    public synchronized void start() {
        if (getState() != State.NEW) {
            throw new IllegalStateException("Thread already started");
        }
        try {
            watchService = FileSystems.getDefault().newWatchService();
            nodePath.getParent().register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        } catch (IOException e) {
            MCDRCompletion.LOGGER.error("Failed to start watch service, you may need to manually invoke reload command to reload command tree.", e);
        }

        super.start();
    }

    @Override
    public void run() {
        super.run();
        WatchKey key;
        try {
            AtomicBoolean reloading = new AtomicBoolean(false);
            while ((key = watchService.take()) != null) {
                if (key.pollEvents().stream()
                        .filter(Objects::nonNull)
                        .filter(event -> event.context() instanceof Path)
                        .map(event -> (Path) event.context())
                        .anyMatch(path -> path.getFileName().toString().equals(nodePath.getFileName().toString()))) {
                    if (reloading.compareAndSet(false, true)) {
                        EXECUTOR.schedule(() -> {
                            reloading.set(true);
                            MCDRCompletion.LOGGER.info("MCDR command tree updated, reloading...");
                            MCDRCompletionServer.loadNodeData();
                        }, 100, TimeUnit.MILLISECONDS);
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        MCDRCompletion.LOGGER.warn("Watch service exited, you may need to manually invoke reload command to reload command tree.");
    }
}
