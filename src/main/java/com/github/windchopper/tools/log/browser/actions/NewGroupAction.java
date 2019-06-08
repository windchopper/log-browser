package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import com.github.windchopper.tools.log.browser.configuration.ConnectionNode;
import com.github.windchopper.tools.log.browser.configuration.GroupNode;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;

import java.util.List;

public class NewGroupAction extends ConfigurationTreeAction {

    private TreeItem<ConfigurationNode> parentNode;

    public NewGroupAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.newGroup"));
        setHandler(event -> {
            GroupNode groupNode = new GroupNode();
            groupNode.setName("New group #" + System.currentTimeMillis());

            TreeItem<ConfigurationNode> connectionNodeItem = new TreeItem<>();
            connectionNodeItem.setValue(groupNode);

            parentNode.getChildren().add(connectionNodeItem);
            parentNode.setExpanded(true);

            MultipleSelectionModel<TreeItem<ConfigurationNode>> selectionModel = getView().getSelectionModel();

            selectionModel.clearSelection();
            selectionModel.select(connectionNodeItem);
        });
    }

    @Override public void prepare(List<TreeItem<ConfigurationNode>> selectedItems) {
        parentNode = selectedItems.size() != 1 ? null : selectedItems.get(0).getValue() instanceof ConnectionNode ? null : selectedItems.get(0);
        disabledProperty().set(parentNode == null);
    }

}
