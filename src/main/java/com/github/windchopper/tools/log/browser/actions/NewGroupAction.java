package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.*;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;

public class NewGroupAction extends ConfigurationTreeAction {

    private TreeItem<ConfigurationNode> parentItem;

    public NewGroupAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.newGroup"));
        setHandler(event -> {
            GroupNode groupNode = ((GroupNode) parentItem.getValue()).addGroup();
            groupNode.setName("New group #" + System.currentTimeMillis());

            TreeItem<ConfigurationNode> connectionNodeItem = new TreeItem<>();
            connectionNodeItem.setValue(groupNode);

            ObservableList<TreeItem<ConfigurationNode>> itemContainer = parentItem.getChildren();
            int targetIndex = itemContainer.size();

            for (int i = 0, count = itemContainer.size(); i < count; i++) {
                if (itemContainer.get(i).getValue() instanceof ConnectionNode) {
                    targetIndex = i;
                    break;
                }
            }

            itemContainer.add(targetIndex, connectionNodeItem);
            parentItem.setExpanded(true);

            MultipleSelectionModel<TreeItem<ConfigurationNode>> selectionModel = view.getSelectionModel();

            selectionModel.clearSelection();
            selectionModel.select(connectionNodeItem);

            mainStageController.saveConfiguration();
        });
    }

    @Override public void prepare() {
        ObservableList<TreeItem<ConfigurationNode>> selectedItems = view.getSelectionModel().getSelectedItems();
        parentItem = selectedItems.size() != 1 ? null : selectedItems.get(0).getValue() instanceof ConnectionNode ? null : selectedItems.get(0);
        disabledProperty().set(parentItem == null);
    }

}
