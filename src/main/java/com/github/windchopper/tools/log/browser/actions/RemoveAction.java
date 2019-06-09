package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RemoveAction extends ConfigurationTreeAction {

    public RemoveAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.remove"));
        setHandler(event -> {

        });
    }

    @Override public void prepare() {

    }

}
