package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Builder;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ConfigurationTreeCell extends TreeCell<ConfigurationNode> {

    private static final Image configurationImage = new Image("/com/github/windchopper/tools/log/browser/images/gear-16.png");
    private static final Image groupImage = new Image("/com/github/windchopper/tools/log/browser/images/box-16.png");
    private static final Image connectionImage = new Image("/com/github/windchopper/tools/log/browser/images/index-16.png");

    private final Builder<ImageView> configurationImageView = Builder.of(() -> new ImageView(configurationImage));
    private final Builder<ImageView> groupImageView = Builder.of(() -> new ImageView(groupImage));
    private final Builder<ImageView> connectionImageView = Builder.of(() -> new ImageView(connectionImage));

    @Override protected void updateItem(ConfigurationNode configurationNode, boolean empty) {
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