package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;

public abstract class ConfigurationTreeAction extends AppAction {

    private TreeView<ConfigurationNode> view;

    public abstract void prepare(List<TreeItem<ConfigurationNode>> selectedItems);

    public TreeView<ConfigurationNode> getView() {
        return view;
    }

    public void setView(TreeView<ConfigurationNode> view) {
        this.view = view;
    }

}
