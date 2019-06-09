package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import javafx.scene.control.TreeView;

public abstract class ConfigurationTreeAction extends AppAction {

    TreeView<ConfigurationNode> view;

    public abstract void prepare();

    public TreeView<ConfigurationNode> getView() {
        throw new UnsupportedOperationException();
    }

    public void setView(TreeView<ConfigurationNode> view) {
        this.view = view;
    }

}
