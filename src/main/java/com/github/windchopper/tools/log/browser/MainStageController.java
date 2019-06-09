package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.actions.AppAction;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import com.github.windchopper.tools.log.browser.configuration.GroupNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;

@ApplicationScoped @FXMLResource(Forms.FXML__MAIN) @Named("MainStageController") public class MainStageController extends AnyStageController {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    @Inject ConfigurationAccess configurationAccess;

    @FXML private TreeView<ConfigurationNode> configurationTreeView;
    @FXML private TreeItem<ConfigurationNode> configurationTreeRoot;
    @FXML private BorderPane workareaPane;

    @Override
    protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        loadWithConfigurationNode(configurationTreeRoot, configurationAccess.getConfiguration());

        stage.setTitle(bundle.getString("com.github.windchopper.tools.log.browser.main.title"));
        stage.setOnCloseRequest(event -> AppAction.shutdownExecutor());

        configurationTreeView.setOnEditCommit(editEvent -> AppAction.executor.execute(this::saveConfiguration));
    }

    private <T extends ConfigurationNode> void loadWithConfigurationNode(TreeItem<ConfigurationNode> item, T configurationNode) {
        item.setValue(configurationNode);

        if (configurationNode instanceof GroupNode) {
            Optional.ofNullable(((GroupNode) configurationNode).getGroups())
                .orElseGet(Collections::emptyList)
                .forEach(groupNode -> loadWithConfigurationNode(
                    Pipeliner.of(TreeItem<ConfigurationNode>::new)
                        .accept(groupItem -> item.getChildren().add(groupItem))
                        .get(),
                    groupNode));

            Optional.ofNullable(((GroupNode) configurationNode).getConnections())
                .orElseGet(Collections::emptyList)
                .forEach(connectionNode -> loadWithConfigurationNode(
                    Pipeliner.of(TreeItem<ConfigurationNode>::new)
                        .accept(connectionItem -> item.getChildren().add(connectionItem))
                        .get(),
                    connectionNode));
        }
    }

    public void saveConfiguration() {
        try {
            configurationAccess.saveConfiguration();
        } catch (Exception thrown) {
            String message = ExceptionUtils.getRootCauseMessage(thrown);
            logger.log(Level.SEVERE, message, thrown);
            Platform.runLater(() -> prepareAlert(() -> new Alert(Alert.AlertType.ERROR, message))
                .show());
        }
    }

    public Stage getStage() {
        return stage;
    }

}