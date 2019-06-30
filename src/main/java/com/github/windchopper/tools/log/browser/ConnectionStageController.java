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
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.nio.file.FileSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@ApplicationScoped @FXMLResource(Globals.FXML__CONNECTION) @Named("ConnectionStageController") public class ConnectionStageController extends BaseStageController {

    @FXML private TextField nameField;
    @FXML private ComboBox<ConnectionType> typeBox;
    @FXML private TextField hostField;
    @FXML private Spinner<Number> portSpinner;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button testButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Connection connection;

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        typeBox.getItems().addAll(ConnectionType.values());
        typeBox.setConverter(new DelegatingStringConverter<>(ConnectionType::displayName));
        typeBox.setCellFactory(CellFactories.listCellFactory((cell, item) -> cell.setText(item.displayName())));

        portSpinner.setValueFactory(new FlexibleSpinnerValueFactory<>(NumberType.INTEGER, 0, 65535, 0));

        connection = (Connection) parameters.get("connection");

        if (connection != null) {
            nameField.setText(connection.getName());
            typeBox.setValue(connection.getType());
            hostField.setText(connection.getHost());
            portSpinner.getValueFactory().setValue(connection.getPort());
            usernameField.setText(connection.getUsername());
            passwordField.setText(connection.getPassword());
        }
    }

    private void runWithExecutor(Collection<BooleanProperty> actionDisableProperties, Runnable action) {
        executor.execute(() -> {
            stage.getScene().setCursor(Cursor.WAIT);
            actionDisableProperties.forEach(property -> property.set(true));

            try {
                action.run();
            } finally {
                actionDisableProperties.forEach(property -> property.set(false));
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
        });
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

    @FXML public void typeSelected(ActionEvent event) {
        portSpinner.getValueFactory().setValue(typeBox.getValue().defaultPort());
    }

    @FXML public void testSelected(ActionEvent event) {
        ConnectionType connectionType = typeBox.getValue();

        if (connectionType == null) {
            errorAlert(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.test.typeNotSelected"));
            return;
        }

        List<BooleanProperty> disableProperties = List.of(
            nameField.disableProperty(),
            typeBox.disableProperty(),
            hostField.disableProperty(),
            portSpinner.disableProperty(),
            usernameField.disableProperty(),
            passwordField.disableProperty(),
            testButton.disableProperty(),
            saveButton.disableProperty(),
            cancelButton.disableProperty());

        runWithExecutor(disableProperties, () -> {
            try (FileSystem ignored = connectionType.newFileSystem(usernameField.getText(), passwordField.getText(), hostField.getText(), portSpinner.getValue().intValue(), "/")) {
                Platform.runLater(() -> informationAlert(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.test.succeeded")));
            } catch (Exception thrown) {
                Platform.runLater(() -> errorAlert(String.format(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.test.failed"), thrown.getLocalizedMessage())));
            }
        });
    }

    @FXML public void saveSelected(ActionEvent event) {
        connection.setName(nameField.getText());
        connection.setType(typeBox.getValue());
        connection.setHost(hostField.getText());
        connection.setPort(portSpinner.getValue().intValue());
        connection.setUsername(usernameField.getText());
        connection.setPassword(passwordField.getText());
        stage.close();
    }

    @FXML public void cancelSelected(ActionEvent event) {
        stage.close();
    }

}
