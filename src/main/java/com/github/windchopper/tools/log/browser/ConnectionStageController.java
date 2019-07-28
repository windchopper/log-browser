package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.CellFactories;
import com.github.windchopper.common.fx.DelegatingStringConverter;
import com.github.windchopper.common.fx.form.Form;
import com.github.windchopper.common.fx.form.FormLoad;
import com.github.windchopper.common.fx.form.StageFormLoad;
import com.github.windchopper.common.fx.spinner.FlexibleSpinnerValueFactory;
import com.github.windchopper.common.fx.spinner.NumberType;
import com.github.windchopper.common.util.Builder;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import com.github.windchopper.tools.log.browser.configuration.ConnectionType;
import com.github.windchopper.tools.log.browser.events.ConfigurationSave;
import com.github.windchopper.tools.log.browser.events.PathListConfirm;
import com.github.windchopper.tools.log.browser.fs.RemoteFileSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped @Form(Globals.FXML__CONNECTION) @Named("ConnectionStageController") public class ConnectionStageController extends BaseStageController {

    @Inject private Event<FormLoad> fxmlResourceOpenEvent;
    @Inject private Event<ConfigurationSave> saveConfigurationEvent;

    @Inject private AsyncRunner asyncRunner;

    @FXML private TextField nameField;
    @FXML private ComboBox<ConnectionType> typeBox;
    @FXML private TextField hostField;
    @FXML private Spinner<Number> portSpinner;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextArea pathListArea;

    @FXML private Button choosePathListButton;

    private Connection connection;

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> formNamespace) {
        super.afterLoad(form, parameters, formNamespace);

        stage.setResizable(false);

        typeBox.getItems().addAll(ConnectionType.values());
        typeBox.setConverter(new DelegatingStringConverter<>(ConnectionType::displayName));
        typeBox.setCellFactory(CellFactories.listCellFactory((cell, item, empty) -> cell.setText(empty || item == null ? null : item.displayName())));

        portSpinner.setValueFactory(new FlexibleSpinnerValueFactory<>(NumberType.INTEGER, 0, 65535, 0));

        connection = (Connection) parameters.get("connection");

        if (connection != null) {
            nameField.setText(connection.getName());
            typeBox.setValue(connection.getType());
            hostField.setText(connection.getHost());
            portSpinner.getValueFactory().setValue(connection.getPort());
            usernameField.setText(connection.getUsername());
            passwordField.setText(connection.getPassword());
            pathListArea.setText(String.join("; ", Optional.ofNullable(connection.getPathList())
                .orElseGet(Collections::emptyList)));
        }
    }

    private RemoteFileSystem newFileSystem() throws IOException {
        ConnectionType connectionType = typeBox.getValue();

        if (connectionType == null) {
            throw new IllegalStateException(Globals.bundle.getString("com.github.windchopper.tools.log.browser.connection.type.notSelected"));
        }

        return connectionType.newFileSystem(
            hostField.getText(),
            portSpinner.getValue().intValue(),
            usernameField.getText(),
            passwordField.getText());
    }

    void pathListConfirmed(@Observes PathListConfirm confirmPathList) {
        pathListArea.setText(String.join("; ", confirmPathList.paths()));
    }

    @FXML public void typeSelected(ActionEvent event) {
        portSpinner.getValueFactory().setValue(typeBox.getValue().defaultPort());
    }

    @FXML public void savePressed(ActionEvent event) {
        connection.setName(nameField.getText());
        connection.setType(typeBox.getValue());
        connection.setHost(hostField.getText());
        connection.setPort(portSpinner.getValue().intValue());
        connection.setUsername(usernameField.getText());
        connection.setPassword(passwordField.getText());
        connection.setPathList(List.of(StringUtils.trimToEmpty(pathListArea.getText())
            .split("\\s*?[;]\\s*?")));

        saveConfigurationEvent.fire(new ConfigurationSave());

        stage.close();
    }

    @FXML public void choosePathListButton(ActionEvent event) {
        asyncRunner.runAsyncWithBusyPointer(stage, List.of(choosePathListButton.disableProperty()), () -> {
            try {
                RemoteFileSystem fileSystem = newFileSystem();
                fxmlResourceOpenEvent.fire(
                    new StageFormLoad(
                        Builder.of(Stage::new)
                            .set(stage -> stage::initOwner, stage)
                            .set(stage -> stage::initModality, Modality.WINDOW_MODAL),
                        Globals.FXML__BROWSE,
                        Map.of("fileSystem", fileSystem)));
            } catch (IOException thrown) {
                errorLogAndAlert(thrown);
            }
        });
    }

}
