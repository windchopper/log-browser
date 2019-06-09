package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Builder;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class ConfigurationTreeCell extends TextFieldTreeCell<ConfigurationNode> {

    private static final Image configurationImage = new Image("/com/github/windchopper/tools/log/browser/images/gear-16.png");
    private static final Image groupImage = new Image("/com/github/windchopper/tools/log/browser/images/box-16.png");
    private static final Image connectionImage = new Image("/com/github/windchopper/tools/log/browser/images/plug-16.png");

    private final Builder<ImageView> configurationImageView = Builder.of(() -> new ImageView(configurationImage));
    private final Builder<ImageView> groupImageView = Builder.of(() -> new ImageView(groupImage));
    private final Builder<ImageView> connectionImageView = Builder.of(() -> new ImageView(connectionImage));

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

        if (configurationNode instanceof ConfigurationRoot) {
            setGraphic(configurationImageView.get());
        } else if (configurationNode instanceof GroupNode) {
            setGraphic(groupImageView.get());
        } else if (configurationNode instanceof ConnectionNode) {
            setGraphic(connectionImageView.get());
        } else {
            setGraphic(null);
        }
    }

}