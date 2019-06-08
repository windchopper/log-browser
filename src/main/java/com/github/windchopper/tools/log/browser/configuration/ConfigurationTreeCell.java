package com.github.windchopper.tools.log.browser.configuration;

import javafx.scene.control.TreeCell;

public class ConfigurationTreeCell extends TreeCell<ConfigurationNode> {

    @Override protected void updateItem(ConfigurationNode configurationNode, boolean empty) {
        super.updateItem(configurationNode, empty);
        setText(configurationNode == null ? null : configurationNode.getName());
    }

}