package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImportConfigurationAction extends AppAction {

    public ImportConfigurationAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.importConfiguration"));
        graphicProperty().set(Pipeliner.of(ImageView::new)
            .set(view -> view::setImage, new Image("/com/github/windchopper/tools/log/browser/images/import-16.png"))
            .get());

        setHandler(event -> {

        });
    }

}
