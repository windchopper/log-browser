package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.CellFactories;
import com.github.windchopper.common.fx.DelegatingStringConverter;
import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.fx.spinner.FlexibleSpinnerValueFactory;
import com.github.windchopper.common.fx.spinner.NumberType;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import com.github.windchopper.tools.log.browser.configuration.ConnectionType;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static java.util.stream.Collectors.toList;

@ApplicationScoped @FXMLResource(Globals.FXML__CONNECTION) @Named("ConnectionStageController") public class ConnectionStageController extends BaseStageController {

    @Inject private Event<SaveConfiguration> saveConfigurationEvent;
    @Inject private AsyncRunner asyncRunner;

    @FXML private GridPane rootPane;
    @FXML private TitledPane parameterPane;
    @FXML private TitledPane browsePane;

    @FXML private TextField nameField;
    @FXML private ComboBox<ConnectionType> typeBox;
    @FXML private TextField hostField;
    @FXML private Spinner<Number> portSpinner;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button testButton;
    @FXML private ToggleButton browseButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private TextField pathField;
    @FXML private TreeView<Path> directoryTreeView;
    @FXML private ListView<Path> fileListView;

    private FileSystem fileSystem;

    private List<BooleanProperty> allComponentDisableProperties;
    private Connection connection;

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
        stage.setResizable(false);

        typeBox.getItems().addAll(ConnectionType.values());
        typeBox.setConverter(new DelegatingStringConverter<>(ConnectionType::displayName));
        typeBox.setCellFactory(CellFactories.listCellFactory((cell, item) -> cell.setText(item.displayName())));

        portSpinner.setValueFactory(new FlexibleSpinnerValueFactory<>(NumberType.INTEGER, 0, 65535, 0));

        directoryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        directoryTreeView.setCellFactory(CellFactories.treeCellFactory((cell, item) -> cell.setText(Optional.ofNullable(item.getFileName())
            .orElse(item).toString())));
        directoryTreeView.getSelectionModel().selectedItemProperty().addListener((observable, unselectedItem, selectedItem) -> {
            if (fileSystem != null) {
                if (selectedItem != null) {
                    Path selectedPath = selectedItem.getValue();
                    pathField.setText(selectedPath.toAbsolutePath().toString());

                    asyncRunner.runAsync(stage, List.of(pathField.disableProperty(), directoryTreeView.disableProperty(), fileListView.disableProperty()), () -> {
                        List<Path> filePaths;

                        try {
                            filePaths = Files.list(selectedPath)
                                .collect(toList());

                            Platform.runLater(() -> {
                                fileListView.getItems().clear();

                                selectedItem.getChildren().clear();
                                selectedItem.setExpanded(true);

                                for (Path path : filePaths) {
                                    if (Files.isDirectory(path)) {
                                        selectedItem.getChildren().add(new TreeItem<>(path));
                                    } else {
                                        fileListView.getItems().add(path);
                                    }
                                }
                            });
                        } catch (IOException thrown) {
                            Platform.runLater(() -> errorAlert(String.format(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.failed"), thrown.getLocalizedMessage())));
                        }
                    });
                }
            }
        });

        fileListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fileListView.setCellFactory(CellFactories.listCellFactory((cell, item) -> cell.setText(Optional.ofNullable(item.getFileName())
            .orElse(item).toString())));

        allComponentDisableProperties = List.of(
            nameField.disableProperty(),
            typeBox.disableProperty(),
            hostField.disableProperty(),
            portSpinner.disableProperty(),
            usernameField.disableProperty(),
            passwordField.disableProperty(),
            testButton.disableProperty(),
            browseButton.disableProperty(),
            saveButton.disableProperty(),
            cancelButton.disableProperty());

        connection = (Connection) parameters.get("connection");

        if (connection != null) {
            nameField.setText(connection.getName());
            typeBox.setValue(connection.getType());
            hostField.setText(connection.getHost());
            portSpinner.getValueFactory().setValue(connection.getPort());
            usernameField.setText(connection.getUsername());
            passwordField.setText(connection.getPassword());
        }

        rootPane.getChildren().remove(browsePane);
        stage.sizeToScene();
    }

    private void alert(AlertType alertType, String message) {
        prepareAlert(Pipeliner.of(() -> new Alert(alertType, message, ButtonType.OK))
            .set(alert -> alert::initOwner, stage)
            .set(alert -> alert::initModality, Modality.WINDOW_MODAL))
            .showAndWait();
    }

    private void informationAlert(String message) {
        alert(AlertType.INFORMATION, message);
    }

    private void errorAlert(String message) {
        alert(AlertType.ERROR, message);
    }

    private FileSystem fileSystem() throws IOException {
        ConnectionType connectionType = typeBox.getValue();

        if (connectionType == null) {
            throw new IllegalStateException(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.typeNotSelected"));
        }

        return connectionType.fileSystem(
            usernameField.getText(),
            passwordField.getText(),
            hostField.getText(),
            portSpinner.getValue().intValue());
    }

    @FXML public void typeSelected(ActionEvent event) {
        portSpinner.getValueFactory().setValue(typeBox.getValue().defaultPort());
    }

    @FXML public void testPressed(ActionEvent event) {
        asyncRunner.runAsync(stage, allComponentDisableProperties, () -> {
            try (FileSystem ignored = fileSystem()) {
                Platform.runLater(() -> informationAlert(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.succeeded")));
            } catch (Exception thrown) {
                Platform.runLater(() -> errorAlert(String.format(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.failed"), thrown.getLocalizedMessage())));
            }
        });
    }

    @FXML public void browsePressed(ActionEvent event) {
        if (rootPane.getChildren().contains(browsePane)) {
            rootPane.getChildren().remove(browsePane);
            closeFileSystem();
        } else {
            rootPane.getChildren().add(browsePane);
        }

        stage.sizeToScene();

        asyncRunner.runAsync(stage, allComponentDisableProperties, () -> {
            try {
                fileSystem = fileSystem();
                TreeItem<Path> rootItem = new TreeItem<>();

                for (Path rootPath : fileSystem.getRootDirectories()) {
                    rootItem.getChildren().add(new TreeItem<>(rootPath));
                }

                Platform.runLater(() -> {
                    directoryTreeView.setRoot(rootItem);
                    fileListView.getItems().clear();
                });
            } catch (Exception thrown) {
                Platform.runLater(() -> errorAlert(String.format(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.failed"), thrown.getLocalizedMessage())));
            }
        });
    }

    @FXML public void savePressed(ActionEvent event) {
        connection.setName(nameField.getText());
        connection.setType(typeBox.getValue());
        connection.setHost(hostField.getText());
        connection.setPort(portSpinner.getValue().intValue());
        connection.setUsername(usernameField.getText());
        connection.setPassword(passwordField.getText());
        stage.close();
        saveConfigurationEvent.fire(new SaveConfiguration());
    }

    @FXML public void cancelPressed(ActionEvent event) {
        stage.close();
    }

}
