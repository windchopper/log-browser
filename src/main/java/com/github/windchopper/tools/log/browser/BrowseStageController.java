package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.cdi.form.Form;
import com.github.windchopper.tools.log.browser.events.PathListConfirm;
import com.github.windchopper.tools.log.browser.fx.FileListCell;
import com.github.windchopper.tools.log.browser.fx.FileTreeCell;
import com.github.windchopper.tools.log.browser.fx.RemoteFile;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@ApplicationScoped @Form(Globals.FXML__BROWSE) @Named("BrowseStageController") public class BrowseStageController extends BaseStageController {

    @Inject private Event<PathListConfirm> confirmPathListEvent;
    @Inject private AsyncRunner asyncRunner;

    @FXML private TextField pathField;
    @FXML private TreeView<RemoteFile> directoryTreeView;
    @FXML private ListView<RemoteFile> fileListView;

    private FileSystem fileSystem;

    private TreeItem<RemoteFile> directoryTreeRoot = new TreeItem<>();
    private final Map<String, BooleanProperty> selectedStateBuffer = new HashMap<>();

    boolean loadTree(RemoteFile selectedFile, List<TreeItem<RemoteFile>> directoryItems, List<RemoteFile> files) {
        try {
            Files.list(selectedFile.getPath())
                .sorted()
                .map(RemoteFile::new)
                .peek(files::add)
                .filter(RemoteFile::isDirectory)
                .map(TreeItem::new)
                .forEach(directoryItems::add);
            return true;
        } catch (IOException thrown) {
            errorLogAndAlert(thrown);
            return false;
        }
    }

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.afterLoad(form, parameters, fxmlLoaderNamespace);

        stage.setOnCloseRequest(event -> closeFileSystem());

        fileSystem = (FileSystem) parameters.get("fileSystem");

        directoryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        directoryTreeView.setCellFactory(view -> new FileTreeCell(
            item -> item.getValue().createSelectedProperty(selectedStateBuffer, directoryTreeView, fileListView),
            selectedStateBuffer));

        directoryTreeView.getSelectionModel().selectedItemProperty().addListener((observable, unselectedItem, selectedItem) -> {
            if (fileSystem == null || selectedItem == null) {
                return;
            }

            RemoteFile selectedFile = selectedItem.getValue();
            pathField.setText(selectedFile.getPath().toString());

            selectedItem.getChildren().clear();
            fileListView.getItems().clear();

            asyncRunner.runAsyncWithBusyPointer(stage, List.of(pathField.disableProperty(), directoryTreeView.disableProperty(), fileListView.disableProperty()), () -> {
                List<TreeItem<RemoteFile>> directoryItems = new ArrayList<>();
                List<RemoteFile> files = new ArrayList<>();

                if (loadTree(selectedFile, directoryItems, files)) {
                    Platform.runLater(() -> {
                        fileListView.getItems().addAll(files);
                        selectedItem.getChildren().addAll(directoryItems);
                        selectedItem.setExpanded(true);
                    });
                }
            });
        });

        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileListView.setCellFactory(view -> new FileListCell(
            file -> file.createSelectedProperty(selectedStateBuffer, directoryTreeView, fileListView),
            selectedStateBuffer));

        fileListView.setOnMouseClicked(event -> {
            RemoteFile selectedFile = fileListView.getSelectionModel().getSelectedItem();

            if (selectedFile != null && selectedFile.isDirectory() && event.getClickCount() > 1) {
                directoryTreeView.getSelectionModel().clearSelection();
                fileListView.getItems().clear();

                String path = selectedFile.getPath().normalize().toString();
                pathField.setText(path);

                TreeItem<RemoteFile> treeItem = findTreeItemByPath(directoryTreeView.getRoot(), path);

                if (treeItem != null) {
                    treeItem.getChildren().clear();
                }

                asyncRunner.runAsyncWithBusyPointer(stage, List.of(pathField.disableProperty(), directoryTreeView.disableProperty(), fileListView.disableProperty()), () -> {
                    List<TreeItem<RemoteFile>> directoryItems = new ArrayList<>();
                    List<RemoteFile> files = new ArrayList<>();

                    if (loadTree(selectedFile, directoryItems, files)) {
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
                    }
                });
            }
        });

        directoryTreeView.setRoot(directoryTreeRoot);

        StreamSupport.stream(fileSystem.getRootDirectories().spliterator(), false)
            .sorted()
            .map(RemoteFile::new)
            .map(TreeItem::new)
            .forEach(item -> directoryTreeRoot.getChildren().add(item));
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
        if (StringUtils.equals(item.getValue().getPath().toString(), path)) {
            return item;
        }

        for (TreeItem<RemoteFile> childItem : item.getChildren()) {
            if (StringUtils.startsWith(path, childItem.getValue().getPath().toString())) {
                return findTreeItemByPath(childItem, path);
            }
        }

        return null;
    }

    @FXML public void confirmPressed(ActionEvent event) {
        stage.close();
        confirmPathListEvent.fire(new PathListConfirm(selectedStateBuffer.entrySet().stream()
            .filter(entry -> entry.getValue().get())
            .map(Entry::getKey)
            .collect(toList())));
    }

}
