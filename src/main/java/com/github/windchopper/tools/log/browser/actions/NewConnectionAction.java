package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import com.github.windchopper.tools.log.browser.configuration.ConnectionNode;
import com.github.windchopper.tools.log.browser.configuration.GroupNode;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;

public class NewConnectionAction extends ConfigurationTreeAction {

    private TreeItem<ConfigurationNode> parentItem;
    private TreeItem<ConfigurationNode> sourceItem;

    public NewConnectionAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.newConnection"));
        setHandler(event -> {
            ConnectionNode connectionNode = ((GroupNode) parentItem.getValue()).addConnection();

            if (sourceItem != null) {
                connectionNode.setName("Copy #" + System.currentTimeMillis() + " of " + sourceItem.getValue().getName());
            } else {
                connectionNode.setName("New connection #" + System.currentTimeMillis());
            }

            TreeItem<ConfigurationNode> connectionNodeItem = new TreeItem<>();
            connectionNodeItem.setValue(connectionNode);

            parentItem.getChildren().add(connectionNodeItem);
            parentItem.setExpanded(true);

            MultipleSelectionModel<TreeItem<ConfigurationNode>> selectionModel = view.getSelectionModel();

            selectionModel.clearSelection();
            selectionModel.select(connectionNodeItem);

            mainStageController.saveConfiguration();
        });
    }

    @Override public void prepare() {
        ObservableList<TreeItem<ConfigurationNode>> selectedItems = view.getSelectionModel().getSelectedItems();

        sourceItem = null;
        parentItem = selectedItems.size() != 1 ? null : selectedItems.get(0);

        if (parentItem != null && parentItem.getValue() instanceof ConnectionNode) {
            sourceItem = parentItem;
            parentItem = parentItem.getParent();
        }

        disabledProperty().set(parentItem == null);
    }

}
