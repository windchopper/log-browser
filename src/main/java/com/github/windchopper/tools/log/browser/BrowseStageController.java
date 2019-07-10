package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.tools.log.browser.events.ConfirmPaths;
import com.github.windchopper.tools.log.browser.fs.RemoteFile;
import com.github.windchopper.tools.log.browser.fs.RemoteFileSystem;
import com.github.windchopper.tools.log.browser.fx.FileListCell;
import com.github.windchopper.tools.log.browser.fx.FileTreeCell;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import static java.util.stream.Collectors.toList;

@ApplicationScoped @FXMLResource(Globals.FXML__BROWSE) @Named("BrowseStageController") public class BrowseStageController extends BaseStageController {

    @Inject private Event<ConfirmPaths> confirmPathListEvent;
    @Inject private AsyncRunner asyncRunner;

    @FXML private TextField pathField;
    @FXML private TreeView<RemoteFile> directoryTreeView;
    @FXML private ListView<RemoteFile> fileListView;

    private RemoteFileSystem fileSystem;

    private final Map<String, BooleanProperty> selectedStateBuffer = new HashMap<>();

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);
        stage.setOnCloseRequest(event -> closeFileSystem());

        fileSystem = (RemoteFileSystem) parameters.get("fileSystem");

        directoryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        directoryTreeView.setCellFactory(view -> new FileTreeCell(
            item -> item.getValue().createSelectedProperty(selectedStateBuffer, directoryTreeView, fileListView),
            selectedStateBuffer));

        directoryTreeView.getSelectionModel().selectedItemProperty().addListener((observable, unselectedItem, selectedItem) -> {
            if (fileSystem == null || selectedItem == null) {
                return;
            }

            RemoteFile selectedFile = selectedItem.getValue();
            pathField.setText(selectedFile.path());

            selectedItem.getChildren().clear();
            fileListView.getItems().clear();

            asyncRunner.runAsync(stage, List.of(pathField.disableProperty(), directoryTreeView.disableProperty(), fileListView.disableProperty()), () -> {
                List<TreeItem<RemoteFile>> directoryItems = new ArrayList<>();
                List<RemoteFile> files = new ArrayList<>();

                try {
                    fileSystem.children(selectedFile.path()).stream()
                        .sorted()
                        .peek(files::add)
                        .filter(RemoteFile::directory)
                        .filter(file -> !file.path().endsWith(".."))
                        .map(TreeItem::new)
                        .forEach(directoryItems::add);
                } catch (IOException thrown) {
                    errorLogAndAlert(thrown);
                    return;
                }

                Platform.runLater(() -> {
                    fileListView.getItems().addAll(files);
                    selectedItem.getChildren().addAll(directoryItems);
                    selectedItem.setExpanded(true);
                });
            });
        });

        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileListView.setCellFactory(view -> new FileListCell(
            file -> file.createSelectedProperty(selectedStateBuffer, directoryTreeView, fileListView),
            selectedStateBuffer));

        fileListView.setOnMouseClicked(event -> {
            RemoteFile selectedFile = fileListView.getSelectionModel().getSelectedItem();

            if (selectedFile != null && selectedFile.directory() && event.getClickCount() > 1) {
                directoryTreeView.getSelectionModel().clearSelection();
                fileListView.getItems().clear();

                String path = fileSystem.normalizePath(selectedFile.path());
                pathField.setText(path);

                TreeItem<RemoteFile> treeItem = findTreeItemByPath(directoryTreeView.getRoot(), path);

                if (treeItem != null) {
                    treeItem.getChildren().clear();
                }

                asyncRunner.runAsync(stage, List.of(pathField.disableProperty(), directoryTreeView.disableProperty(), fileListView.disableProperty()), () -> {
                    List<TreeItem<RemoteFile>> directoryItems = new ArrayList<>();
                    List<RemoteFile> files = new ArrayList<>();

                    try {
                        fileSystem.children(path).stream()
                            .sorted()
                            .peek(files::add)
                            .filter(RemoteFile::directory)
                            .filter(file -> !file.path().endsWith(".."))
                            .map(TreeItem::new)
                            .forEach(directoryItems::add);
                    } catch (IOException thrown) {
                        errorLogAndAlert(thrown);
                        return;
                    }

                    Platform.runLater(() -> {
                        fileListView.getItems().addAll(files);

                        if (treeItem != null) {
                            treeItem.getChildren().addAll(directoryItems);

                            for (TreeItem<RemoteFile> item = treeItem; item != null; item = item.getParent()) {
                                item.setExpanded(true);
                            }

                            directoryTreeView.scrollTo(directoryTreeView.getRow(treeItem));
                            directoryTreeView.getSelectionModel().select(treeItem);
                        }
                    });
                });
            }
        });

        try {
            directoryTreeView.setRoot(new TreeItem<>(fileSystem.root()));
        } catch (IOException thrown) {
            errorLogAndAlert(thrown);
        }
    }

    @PreDestroy void closeFileSystem() {
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException thrown) {
                logger.log(Level.SEVERE, ExceptionUtils.getRootCauseMessage(thrown), thrown);
            }
        }
    }

    private TreeItem<RemoteFile> findTreeItemByPath(TreeItem<RemoteFile> item, String path) {
        if (StringUtils.equals(item.getValue().path(), path)) {
            return item;
        }

        for (TreeItem<RemoteFile> childItem : item.getChildren()) {
            if (StringUtils.startsWith(path, childItem.getValue().path())) {
                return findTreeItemByPath(childItem, path);
            }
        }

        return null;
    }

    @FXML public void confirmPressed(ActionEvent event) {
        stage.close();
        confirmPathListEvent.fire(new ConfirmPaths(selectedStateBuffer.entrySet().stream()
            .filter(entry -> entry.getValue().get())
            .map(Entry::getKey)
            .collect(toList())));
    }

}
