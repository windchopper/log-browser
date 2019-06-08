package com.github.windchopper.tools.log.browser.configuration;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class ConfigurationTreeCellFactory implements Callback<TreeView<ConfigurationNode>, TreeCell<ConfigurationNode>> {

    @Override public TreeCell<ConfigurationNode> call(TreeView<ConfigurationNode> view) {
        return new ConfigurationTreeCell();
    }

}
