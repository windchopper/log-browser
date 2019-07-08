package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.CellFactories;
import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.fx.event.FXMLResourceOpen;
import com.github.windchopper.common.util.Builder;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.configuration.Configuration;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationElement;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import com.github.windchopper.tools.log.browser.configuration.Group;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static java.util.function.Predicate.not;

@ApplicationScoped @FXMLResource(Globals.FXML__MAIN) @Named("MainStageController") public class MainStageController extends BaseStageController {

    @Inject private Event<FXMLResourceOpen> fxmlResourceOpenEvent;
    @Inject private Event<SaveConfiguration> saveConfigurationEvent;
    @Inject private ConfigurationAccess configurationAccess;
    @Inject private AsyncRunner asyncRunner;

    @FXML private TreeView<ConfigurationElement> configurationTreeView;
    @FXML private TreeItem<ConfigurationElement> configurationTreeRoot;
    @FXML private MenuButton configurationButton;
    @FXML private MenuItem importConfigurationMenuItem;
    @FXML private MenuItem exportConfigurationMenuItem;
    @FXML private Button gatherButton;
    @FXML private MenuItem addGroupMenuItem;
    @FXML private MenuItem addConnectionMenuItem;
    @FXML private MenuItem removeMenuItem;
    @FXML private MenuItem groupMenuItem;
    @FXML private MenuItem ungroupMenuItem;
    @FXML private MenuItem propertiesMenuItem;
    @FXML private BorderPane workareaPane;

    @Override
    protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        stage.setTitle(Globals.bundle.getString("com.github.windchopper.tools.log.browser.main.title"));

        loadWithConfigurationNode(configurationTreeRoot, configurationAccess.getConfiguration());

        configurationTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configurationTreeView.setCellFactory(CellFactories.treeCellFactory((cell, item, empty) -> cell.setText(empty || item == null ? null : item.getName())));
        configurationTreeView.setOnEditCommit(editEvent -> saveConfigurationEvent.fire(new SaveConfiguration()));
    }

    private <T extends ConfigurationElement> void loadWithConfigurationNode(TreeItem<ConfigurationElement> item, T configurationNode) {
        item.setValue(configurationNode);

        if (configurationNode instanceof Group) {
            Optional.ofNullable(((Group) configurationNode).getGroups())
                .orElseGet(Collections::emptyList)
                .forEach(group -> loadWithConfigurationNode(
                    Pipeliner.of(TreeItem<ConfigurationElement>::new)
                        .accept(groupItem -> item.getChildren().add(groupItem))
                        .get(),
                    group));

            Optional.ofNullable(((Group) configurationNode).getConnections())
                .orElseGet(Collections::emptyList)
                .forEach(connection -> loadWithConfigurationNode(
                    Pipeliner.of(TreeItem<ConfigurationElement>::new)
                        .accept(connectionItem -> item.getChildren().add(connectionItem))
                        .get(),
                    connection));
        }
    }

    void saveConfiguration(@Observes SaveConfiguration saveConfiguration) {
        asyncRunner.runAsync(stage, List.of(), () -> {
            try {
                configurationAccess.saveConfiguration();
                configurationTreeView.refresh();
            } catch (Exception thrown) {
                String message = ExceptionUtils.getRootCauseMessage(thrown);
                logger.log(Level.SEVERE, message, thrown);
                Platform.runLater(() -> Pipeliner.of(prepareAlert(() -> new Alert(Alert.AlertType.ERROR, message)))
                    .set(alert -> alert::initOwner, topLevelStage())
                    .set(alert -> alert::initModality, Modality.WINDOW_MODAL)
                    .get()
                    .show());
            }
        });
    }

    @FXML public void contextMenuShowing(WindowEvent event) {
        ObservableList<TreeItem<ConfigurationElement>> selectedItems = configurationTreeView.getSelectionModel().getSelectedItems();

        addGroupMenuItem.setDisable(1 != selectedItems.stream()
            .map(TreeItem::getValue)
            .filter(not(item -> item instanceof Connection))
            .count());
        addConnectionMenuItem.setDisable(1 != selectedItems.size());
        removeMenuItem.setDisable(selectedItems.isEmpty() || selectedItems.stream()
            .map(TreeItem::getValue)
            .anyMatch(item -> item instanceof Configuration));
        propertiesMenuItem.setDisable(1 != selectedItems.stream()
            .map(TreeItem::getValue)
            .filter(not(item -> item instanceof Configuration))
            .count());
    }

    private void openWindow(String fxmlResource, String parameterName, Object parameter) {
        asyncRunner.runAsync(this.stage, List.of(propertiesMenuItem.disableProperty()), () -> fxmlResourceOpenEvent.fire(
            new FXMLResourceOpen(
                Builder.of(Stage::new)
                    .set(stage -> stage::initOwner, this.stage)
                    .set(stage -> stage::initModality, Modality.WINDOW_MODAL),
                fxmlResource,
                Map.of(parameterName, parameter))));
    }

    private void openConnectionWindow(Connection connection) {
        openWindow(Globals.FXML__CONNECTION, "connection", connection);
    }

    private void openGroupWindow(Group group) {
        openWindow(Globals.FXML__GROUP, "group", group);
    }

    @FXML public void gatherPressed(ActionEvent event) {
        List<BooleanProperty> disableProperties = List.of(
            configurationButton.disableProperty(),
            gatherButton.disableProperty(),
            configurationTreeView.disableProperty());

        asyncRunner.runAsync(stage, disableProperties, () -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML public void addGroupSelected(ActionEvent event) {
        MultipleSelectionModel<TreeItem<ConfigurationElement>> selectionModel = configurationTreeView.getSelectionModel();
        TreeItem<ConfigurationElement> parentItem = selectionModel.getSelectedItem();

        if (parentItem.getValue() instanceof Group) {
            Group group = ((Group) parentItem.getValue()).addGroup();
            group.setName(Globals.bundle.getString("com.github.windchopper.tools.log.browser.main.newGroup"));

            TreeItem<ConfigurationElement> connectionItem = new TreeItem<>();
            connectionItem.setValue(group);

            ObservableList<TreeItem<ConfigurationElement>> itemContainer = parentItem.getChildren();
            int targetIndex = itemContainer.size();

            for (int i = 0, count = itemContainer.size(); i < count; i++) {
                if (itemContainer.get(i).getValue() instanceof Connection) {
                    targetIndex = i;
                    break;
                }
            }

            itemContainer.add(targetIndex, connectionItem);
            parentItem.setExpanded(true);

            selectionModel.clearSelection();
            selectionModel.select(connectionItem);

            saveConfigurationEvent.fire(new SaveConfiguration());

            openGroupWindow(group);
        }
    }

    @FXML public void addConnectionSelected(ActionEvent event) {
        MultipleSelectionModel<TreeItem<ConfigurationElement>> selectionModel = configurationTreeView.getSelectionModel();
        TreeItem<ConfigurationElement> parentItem = selectionModel.getSelectedItem();

        if (parentItem.getValue() instanceof Group) {
            Connection connection = ((Group) parentItem.getValue()).addConnection();
            connection.setName(Globals.bundle.getString("com.github.windchopper.tools.log.browser.main.newConnection"));

            TreeItem<ConfigurationElement> connectionItem = new TreeItem<>();
            connectionItem.setValue(connection);

            parentItem.getChildren().add(connectionItem);
            parentItem.setExpanded(true);

            selectionModel.clearSelection();
            selectionModel.select(connectionItem);

            saveConfigurationEvent.fire(new SaveConfiguration());

            openConnectionWindow(connection);
        }
    }

    @FXML public void propertiesSelected(ActionEvent event) {
        TreeItem<ConfigurationElement> selectedItem = configurationTreeView.getSelectionModel().getSelectedItem();

        if (selectedItem.getValue() instanceof Connection) {
            openConnectionWindow((Connection) selectedItem.getValue());
        } else if (selectedItem.getValue() instanceof Group) {
            openGroupWindow((Group) selectedItem.getValue());
        }
    }

}