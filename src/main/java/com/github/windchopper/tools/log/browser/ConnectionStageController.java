package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.DelegatingStringConverter;
import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.fx.spinner.FlexibleSpinnerValueFactory;
import com.github.windchopper.common.fx.spinner.NumberType;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import com.github.windchopper.tools.log.browser.configuration.ConnectionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Map;

@ApplicationScoped @FXMLResource(Globals.FXML__CONNECTION) @Named("ConnectionStageController") public class ConnectionStageController extends BaseStageController {

    public static class ConnectionTypeListCell extends ListCell<ConnectionType> {

        @Override protected void updateItem(ConnectionType item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? null : String.format("%s (%s)", item.description(), item.title()));
        }

    }

    public static class ConnectionTypeListCellFactory implements Callback<ListView<ConnectionType>, ListCell<ConnectionType>> {

        @Override public ListCell<ConnectionType> call(ListView<ConnectionType> param) {
            return new ConnectionTypeListCell();
        }

    }

    @FXML private TextField nameField;
    @FXML private ComboBox<ConnectionType> typeBox;
    @FXML private TextField hostField;
    @FXML private Spinner<Number> portSpinner;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private Connection connection;

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        typeBox.getItems().addAll(ConnectionType.values());
        typeBox.setConverter(new DelegatingStringConverter<>(connectionType -> connectionType == null ? "" : String.format("%s (%s)", connectionType.description(), connectionType.title())));

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

    @FXML public void typeSelected(ActionEvent event) {
        portSpinner.getValueFactory().setValue(typeBox.getValue().defaultPort());
    }

    @FXML public void testSelected(ActionEvent event) {

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
