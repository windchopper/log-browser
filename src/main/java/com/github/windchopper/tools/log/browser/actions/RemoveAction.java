package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RemoveAction extends ConfigurationTreeAction {

    public RemoveAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.remove"));
        graphicProperty().set(Pipeliner.of(ImageView::new)
            .set(view -> view::setImage, new Image("/com/github/windchopper/tools/log/browser/images/delete-16.png"))
            .get());

        setHandler(event -> {

        });
    }

    @Override public void prepare() {

    }

}
