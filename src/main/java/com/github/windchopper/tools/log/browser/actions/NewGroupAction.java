package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import com.github.windchopper.tools.log.browser.configuration.ConnectionNode;
import com.github.windchopper.tools.log.browser.configuration.ContainerNode;
import com.github.windchopper.tools.log.browser.configuration.GroupNode;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NewGroupAction extends ConfigurationTreeAction {

    private TreeItem<ConfigurationNode> parentItem;

    public NewGroupAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.newGroup"));
        graphicProperty().set(Pipeliner.of(ImageView::new)
            .set(view -> view::setImage, new Image("/com/github/windchopper/tools/log/browser/images/box-new-16.png"))
            .get());

        setHandler(event -> {
            GroupNode groupNode = ((ContainerNode) parentItem.getValue()).addGroup();
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
