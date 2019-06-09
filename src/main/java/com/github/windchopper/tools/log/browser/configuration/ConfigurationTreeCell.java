package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Builder;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class ConfigurationTreeCell extends TextFieldTreeCell<ConfigurationNode> {

    ConfigurationTreeCell() {
        super(new StringConverter<>() {

            ConfigurationNode configurationNode;

            @Override public String toString(ConfigurationNode configurationNode) {
                this.configurationNode = configurationNode;
                return configurationNode.getName();
            }

            @Override public ConfigurationNode fromString(String string) {
                configurationNode.setName(string);
                return configurationNode;
            }

        });
    }

    @Override public void updateItem(ConfigurationNode configurationNode, boolean empty) {
        super.updateItem(configurationNode, empty);
        setText(configurationNode == null ? null : configurationNode.getName());
    }

}