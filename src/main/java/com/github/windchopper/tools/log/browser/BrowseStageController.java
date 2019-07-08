package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.CellFactories;
import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.tools.log.browser.events.ConfirmPathList;
import com.github.windchopper.tools.log.browser.fs.RemoteFile;
import com.github.windchopper.tools.log.browser.fs.RemoteFileSystem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@ApplicationScoped @FXMLResource(Globals.FXML__BROWSE) @Named("BrowseStageController") public class BrowseStageController extends BaseStageController {

    @Inject private Event<ConfirmPathList> confirmPathListEvent;
    @Inject private AsyncRunner asyncRunner;

    @FXML private TextField pathField;
    @FXML private TreeView<RemoteFile> directoryTreeView;
    @FXML private ListView<RemoteFile> fileListView;

    private RemoteFileSystem fileSystem;

    @PreDestroy void closeFileSystem() {
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException thrown) {
                logger.log(Level.SEVERE, ExceptionUtils.getRootCauseMessage(thrown), thrown);
            }
        }
    }

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        fileSystem = (RemoteFileSystem) parameters.get("fileSystem");

        directoryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        directoryTreeView.setCellFactory(CellFactories.treeCellFactory(RemoteFile::updateCell));
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
        fileListView.setCellFactory(CellFactories.listCellFactory(RemoteFile::updateCell));

        try {
            directoryTreeView.setRoot(new TreeItem<>(fileSystem.root()));
        } catch (IOException thrown) {
            errorLogAndAlert(thrown);
        }
    }

    @FXML public void confirmPressed(ActionEvent event) {
        stage.close();
        confirmPathListEvent.fire(new ConfirmPathList(
            List.of("bla", "bla", "bla")));
    }

}
