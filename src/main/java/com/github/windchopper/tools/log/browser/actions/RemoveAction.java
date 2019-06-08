package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import javafx.scene.control.TreeItem;

import java.util.List;

public class RemoveAction extends ConfigurationTreeAction {

    public RemoveAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.remove"));
        setHandler(event -> {

        });
    }

    @Override public void prepare(List<TreeItem<ConfigurationNode>> selectedItems) {

    }

}
