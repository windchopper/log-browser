package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import com.github.windchopper.tools.log.browser.configuration.ConnectionNode;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NewConnectionAction extends ConfigurationTreeAction {

    private TreeItem<ConfigurationNode> parentItem;
    private TreeItem<ConfigurationNode> sourceItem;

    public NewConnectionAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.newConnection"));
        graphicProperty().set(Pipeliner.of(ImageView::new)
            .set(view -> view::setImage, new Image("/com/github/windchopper/tools/log/browser/images/index-new-16.png"))
            .get());

        setHandler(event -> {
            ConnectionNode connectionNode = new ConnectionNode();

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
