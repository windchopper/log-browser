package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.DelegatingStringConverter;
import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.tools.log.browser.events.ConfirmPaths;
import com.github.windchopper.tools.log.browser.fs.RemoteFile;
import com.github.windchopper.tools.log.browser.fs.RemoteFileSystem;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static java.util.stream.Collectors.toList;

@ApplicationScoped @FXMLResource(Globals.FXML__BROWSE) @Named("BrowseStageController") public class BrowseStageController extends BaseStageController {

    private interface FileCell {

        default BooleanProperty createSelectedProperty(Map<String, BooleanProperty> selectedProperties, RemoteFile file, TreeView<?> treeView, ListView<?> listView) {
            BooleanProperty selectedProperty = selectedProperties.computeIfAbsent(file.path(), missingPath -> new SimpleBooleanProperty(file, "selected"));

            selectedProperty.addListener((observable, oldValue, newValue) -> {
                treeView.refresh();
                listView.refresh();
            });

            return selectedProperty;
        }

        default Paint fileTextFill(Map<String, BooleanProperty> selectedProperties, RemoteFile file) {
            return selectedProperties.entrySet().stream()
                .filter(entry -> entry.getValue().get() && StringUtils.startsWith(entry.getKey(), file.path()))
                .findFirst()
                .map(entry -> Color.DARKGREEN)
                .orElse(Color.BLACK);
        }

    }

    private class FileListCell extends CheckBoxListCell<RemoteFile> implements FileCell {

        FileListCell() {
            setConverter(new DelegatingStringConverter<>(file -> file.displayName(true)));
            setSelectedStateCallback(this::fileSelectedProperty);
        }

        private BooleanProperty fileSelectedProperty(RemoteFile file) {
            return createSelectedProperty(selectedProperties, file, directoryTreeView, fileListView);
        }

        @Override public void updateItem(RemoteFile file, boolean empty) {
            super.updateItem(file, empty);
            if (!empty) {
                try {
                    CheckBox checkBox = (CheckBox) FieldUtils.readField(this, "checkBox", true);
                    checkBox.setVisible(file.directory());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                setTextFill(fileTextFill(selectedProperties, file));
            }
        }

    }

    private class FileTreeCell extends CheckBoxTreeCell<RemoteFile> implements FileCell {

        FileTreeCell() {
            setConverter(new DelegatingStringConverter<>(item -> item.getValue().displayName(false)));
            setSelectedStateCallback(this::fileSelectedProperty);
        }

        private BooleanProperty fileSelectedProperty(TreeItem<RemoteFile> item) {
            return createSelectedProperty(selectedProperties, item.getValue(), directoryTreeView, fileListView);
        }

        @Override public void updateItem(RemoteFile file, boolean empty) {
            super.updateItem(file, empty);
            if (!empty) {
                try {
                    CheckBox checkBox = (CheckBox) FieldUtils.readField(this, "checkBox", true);
                    checkBox.setVisible(file.directory());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                setTextFill(fileTextFill(selectedProperties, file));
            }
        }

    }

    @Inject private Event<ConfirmPaths> confirmPathListEvent;
    @Inject private AsyncRunner asyncRunner;

    @FXML private TextField pathField;
    @FXML private TreeView<RemoteFile> directoryTreeView;
    @FXML private ListView<RemoteFile> fileListView;

    private RemoteFileSystem fileSystem;

    private final Map<String, BooleanProperty> selectedProperties = new HashMap<>();

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        stage.setOnCloseRequest(event -> {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException thrown) {
                    logger.log(Level.SEVERE, ExceptionUtils.getRootCauseMessage(thrown), thrown);
                }
            }
        });

        fileSystem = (RemoteFileSystem) parameters.get("fileSystem");

        directoryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        directoryTreeView.setCellFactory(view -> new FileTreeCell());
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
                    fileSystem.children(selectedFile).stream()
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
                    selectedItem.getChildren().addAll(directoryItems);
                    fileListView.getItems().addAll(files);
                });
            });
        });

        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileListView.setCellFactory(view -> new FileListCell());

        try {
            directoryTreeView.setRoot(new TreeItem<>(fileSystem.root()));
        } catch (IOException thrown) {
            errorLogAndAlert(thrown);
        }
    }

    @FXML public void confirmPressed(ActionEvent event) {
        stage.close();
        confirmPathListEvent.fire(new ConfirmPaths(selectedProperties.entrySet().stream()
            .filter(entry -> entry.getValue().get())
            .map(Map.Entry::getKey)
            .collect(toList())));
    }

}
