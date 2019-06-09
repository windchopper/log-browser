package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.util.KnownSystemProperties;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.common.util.stream.FailableFunction;
import com.github.windchopper.tools.log.browser.actions.AppAction;
import com.github.windchopper.tools.log.browser.configuration.ContainerNode;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationRoot;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped @FXMLResource(Forms.FXML__MAIN) public class MainStageController extends AnyStageController implements PreferencesAware, XmlAware {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");
    private static final Logger logger = Logger.getLogger(MainStageController.class.getName());

    @FXML private TreeItem<ConfigurationNode> configurationTreeRoot;
    @FXML private BorderPane workareaPane;

    private ConfigurationRoot configurationRoot;

    @Override
    protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        loadWithConfigurationNode(configurationTreeRoot, configurationRoot = Optional.of(KnownSystemProperties.userHomePath.get()
            .orElseGet(() -> Paths.get(""))
            .resolve(".log-browser/configuration.xml"))
            .filter(Files::exists)
            .map(FailableFunction.wrap(this::loadConfiguration))
            .flatMap(result -> result
                .onFailure((path, exception) -> logger.log(Level.SEVERE, bundle.getString("com.github.windchopper.tools.log.browser.main.errorLoadingConfiguration"), exception))
                .result())
            .orElseGet(ConfigurationRoot::new));

        if (StringUtils.isBlank(configurationRoot.getName())) {
            configurationRoot.setName(bundle.getString("com.github.windchopper.tools.log.browser.main.newConfiguration"));
        }

        stage.setTitle(bundle.getString("com.github.windchopper.tools.log.browser.main.title"));
        stage.setOnCloseRequest(event -> AppAction.shutdownExecutor());
    }

    private <T extends ConfigurationNode> void loadWithConfigurationNode(TreeItem<ConfigurationNode> item, T configurationNode) {
        item.setValue(configurationNode);

        if (configurationNode instanceof ContainerNode) {
            Optional.ofNullable(((ContainerNode) configurationNode).getGroups())
                .orElseGet(Collections::emptyList)
                .forEach(groupNode -> loadWithConfigurationNode(
                    Pipeliner.of(TreeItem<ConfigurationNode>::new)
                        .accept(groupItem -> item.getChildren().add(groupItem))
                        .get(),
                    groupNode));

            Optional.ofNullable(((ContainerNode) configurationNode).getConnections())
                .orElseGet(Collections::emptyList)
                .forEach(connectionNode -> loadWithConfigurationNode(
                    Pipeliner.of(TreeItem<ConfigurationNode>::new)
                        .accept(connectionItem -> item.getChildren().add(connectionItem))
                        .get(),
                    connectionNode));
        }
    }

}