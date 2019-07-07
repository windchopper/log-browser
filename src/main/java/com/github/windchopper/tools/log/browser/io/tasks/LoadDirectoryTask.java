package com.github.windchopper.tools.log.browser.io.tasks;

import com.github.windchopper.common.fx.CellFactories;
import com.github.windchopper.tools.log.browser.Globals;
import com.github.windchopper.tools.log.browser.io.FileSystemTask;
import javafx.scene.control.TreeCell;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class LoadDirectoryTask extends FileSystemTask {

    private final Path directoryPath;

    private final List<Path> filePaths = new ArrayList<>();
    private final List<Path> subDirectoryPaths = new ArrayList<>();

    private IOException exception;

    public LoadDirectoryTask(Path directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override public void run() {
        try {
            Files.list(directoryPath).forEach(path -> (Files.isDirectory(path) ? filePaths : subDirectoryPaths).add(path));
        } catch (IOException thrown) {
            exception = thrown;
        }
    }

    public static void updateTreeCell(TreeCell<Future<LoadDirectoryTask>> cell, CompletableFuture<LoadDirectoryTask> taskFuture) {
        if (taskFuture.isDone()) {
//            cell.setText(Optional.ofNullable(taskFuture.getNow().directoryPath.getFileName()));
        } else {
            cell.setText(Globals.bundle.getString("com.github.windchopper.tools.log.browser.io.task.loading"));
        }
    }

}
