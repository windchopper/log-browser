package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationElement;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import com.github.windchopper.tools.log.browser.configuration.ConnectionType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private Connection connection;

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        typeBox.getItems().addAll(ConnectionType.values());
        typeBox.setConverter(new StringConverter<ConnectionType>() {
            @Override
            public String toString(ConnectionType connectionType) {
                return connectionType == null ? "" : String.format("%s (%s)", connectionType.description(), connectionType.title());
            }

            @Override
            public ConnectionType fromString(String string) {
                return null;
            }
        });

        connection = (Connection) parameters.get("connection");

        if (connection != null) {
            nameField.setText(connection.getName());
        }
    }

}
