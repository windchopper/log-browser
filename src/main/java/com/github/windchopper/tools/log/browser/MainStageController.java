package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.CellFactories;
import com.github.windchopper.common.fx.form.Form;
import com.github.windchopper.common.fx.form.FormLoad;
import com.github.windchopper.common.fx.form.StageFormLoad;
import com.github.windchopper.common.util.Builder;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.configuration.Configuration;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationElement;
import com.github.windchopper.tools.log.browser.configuration.Connection;
import com.github.windchopper.tools.log.browser.configuration.Group;
import com.github.windchopper.tools.log.browser.events.ConfigurationSave;
import com.github.windchopper.tools.log.browser.events.TabFormLoad;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.function.Predicate.not;

@ApplicationScoped @Form(Globals.FXML__MAIN) @Named("MainStageController") public class MainStageController extends BaseStageController {

    @Inject private Event<FormLoad> formLoadEvent;
    @Inject private Event<ConfigurationSave> configurationSaveEvent;
    @Inject private ConfigurationAccess configurationAccess;
    @Inject private AsyncRunner asyncRunner;

    @FXML private TreeView<ConfigurationElement> configurationTreeView;
    @FXML private TreeItem<ConfigurationElement> configurationTreeRoot;
    @FXML private MenuButton configurationButton;
    @FXML private MenuItem importConfigurationMenuItem;
    @FXML private MenuItem exportConfigurationMenuItem;
    @FXML private MenuItem downloadMenuItem;
    @FXML private MenuItem addGroupMenuItem;
    @FXML private MenuItem addConnectionMenuItem;
    @FXML private MenuItem removeMenuItem;
    @FXML private MenuItem groupMenuItem;
    @FXML private MenuItem ungroupMenuItem;
    @FXML private MenuItem propertiesMenuItem;
    @FXML private TabPane workareaPane;

    @Override protected void afterLoad(Parent form, Map<String, ?> parameters, Map<String, ?> formNamespace) {
        super.afterLoad(form, parameters, formNamespace);

        stage.setTitle(Globals.bundle.getString("com.github.windchopper.tools.log.browser.main.title"));

        loadWithConfigurationNode(configurationTreeRoot, configurationAccess.getConfiguration());

        configurationTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configurationTreeView.setCellFactory(CellFactories.treeCellFactory((cell, item, empty) -> cell.setText(empty || item == null ? null : item.getName())));
        configurationTreeView.setOnEditCommit(editEvent -> configurationSaveEvent.fire(new ConfigurationSave()));
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

    void saveConfiguration(@Observes ConfigurationSave saveConfiguration) {
        asyncRunner.runAsyncWithBusyPointer(stage, List.of(), () -> {
            try {
                configurationAccess.saveConfiguration();
                configurationTreeView.refresh();
            } catch (IOException | JAXBException thrown) {
                errorLogAndAlert(thrown);
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
        asyncRunner.runAsyncWithBusyPointer(this.stage, List.of(propertiesMenuItem.disableProperty()), () -> formLoadEvent.fire(
            new StageFormLoad(
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

    @FXML public void downloadSelected(ActionEvent event) {
        formLoadEvent.fire(new TabFormLoad(
            Pipeliner.of(Tab::new)
                .set(tab -> tab::setText, String.format("%1$tF %1$tT", LocalDateTime.now()))
                .accept(tab -> workareaPane.getTabs().add(tab))
                .accept(tab -> workareaPane.getSelectionModel().select(tab))
                .get(),
            Globals.FXML__DOWNLOAD,
            Map.of("selection", List.copyOf(configurationTreeView.getSelectionModel().getSelectedItems()))));
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

            configurationSaveEvent.fire(new ConfigurationSave());

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

            configurationSaveEvent.fire(new ConfigurationSave());

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

    @FXML public void removeSelected(ActionEvent event) {
        List<TreeItem<ConfigurationElement>> selectedItems = new ArrayList<>(configurationTreeView.getSelectionModel().getSelectedItems());
        configurationTreeView.getSelectionModel().clearSelection();

        while (selectedItems.size() > 0) {
            List<TreeItem<ConfigurationElement>> copyOfSelectedItems = new ArrayList<>(selectedItems);

            if (!selectedItems.removeIf(item -> item.getParent() == null || copyOfSelectedItems.contains(item.getParent()))) {
                break;
            }
        }

        for (TreeItem<ConfigurationElement> item : selectedItems) {
            TreeItem<ConfigurationElement> parentItem = item.getParent();
            Group parentGroup = (Group) parentItem.getValue();

            ConfigurationElement configurationElement = item.getValue();
            parentItem.getChildren().remove(item);

            if (configurationElement instanceof Connection) {
                parentGroup.getConnections().remove(configurationElement);
            } else if (configurationElement instanceof Group) {
                parentGroup.getGroups().remove(configurationElement);
            }
        }

        configurationSaveEvent.fire(new ConfigurationSave());
    }

    @FXML public void groupSelected(ActionEvent event) {

    }

    @FXML public void ungroupSelected(ActionEvent event) {

    }

}