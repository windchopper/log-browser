package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.util.KnownSystemProperties;
import com.github.windchopper.common.util.stream.FailableFunction;
import com.github.windchopper.tools.log.browser.configuration.Configuration;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import com.github.windchopper.tools.log.browser.configuration.ConnectionNode;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped @FXMLResource(Forms.FXML__MAIN) public class MainStageController extends AnyStageController implements PreferencesAware, XmlAware {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");
    private static final Logger logger = Logger.getLogger("com.github.windchopper.tools.log.browser");

    @FXML private TreeView<ConfigurationNode> configurationTreeView;
    @FXML private TreeItem<ConfigurationNode> configurationTreeRoot;

    @FXML private MenuItem newConnectionMenuItem;
    @FXML private MenuItem newGroupMenuItem;
    @FXML private MenuItem removeMenuItem;
    @FXML private MenuItem groupMenuItem;
    @FXML private MenuItem ungroupMenuItem;
    @FXML private MenuItem propertiesMenuItem;

    private Configuration configuration;

    @Override
    protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        configurationTreeRoot.setValue(configuration = Optional.of(KnownSystemProperties.userHomePath.get()
            .orElseGet(() -> Paths.get(""))
            .resolve(".log-browser/configuration.xml"))
            .filter(Files::exists)
            .map(FailableFunction.wrap(this::loadConfiguration))
            .flatMap(result -> result
                .onFailure((path, exception) -> logger.log(Level.SEVERE, bundle.getString("com.github.windchopper.tools.log.browser.main.errorLoadingConfiguration"), exception))
                .result())
            .orElseGet(Configuration::new));

        if (StringUtils.isBlank(configuration.getName())) {
            configuration.setName(bundle.getString("com.github.windchopper.tools.log.browser.main.newConfiguration"));
        }

        ObservableList<TreeItem<ConfigurationNode>> selectedItems = configurationTreeView.getSelectionModel().getSelectedItems();

        Callable<Boolean> anyConnectionSelected = () -> selectedItems.stream()
            .anyMatch(item -> item.getValue() instanceof ConnectionNode);

        newConnectionMenuItem.disableProperty().bind(Bindings.createBooleanBinding(anyConnectionSelected));
        newGroupMenuItem.disableProperty().bind(Bindings.createBooleanBinding(anyConnectionSelected));

        removeMenuItem.disableProperty().bind(Bindings.isNull(configurationTreeView.getSelectionModel().selectedItemProperty()));
        groupMenuItem.disableProperty().bind(Bindings.isNull(configurationTreeView.getSelectionModel().selectedItemProperty()));
        ungroupMenuItem.disableProperty().bind(Bindings.isNull(configurationTreeView.getSelectionModel().selectedItemProperty()));

        propertiesMenuItem.disableProperty().bind(Bindings.createBooleanBinding(() -> selectedItems.size() != 1
            || selectedItems.get(0).getValue() instanceof ConnectionNode));
    }

    @FXML public void importPressed(ActionEvent event) {
        System.out.println("import pressed");
    }

    @FXML public void exportPressed(ActionEvent event) {
        System.out.println("export pressed");
    }

}