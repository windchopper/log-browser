package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.tools.log.browser.configuration.ConfigurationNode;
import javafx.scene.control.TreeItem;

import java.util.List;

public class GroupAction extends ConfigurationTreeAction {

    public GroupAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.group"));
        setHandler(event -> {

        });
    }

    @Override public void prepare() {

    }

}
