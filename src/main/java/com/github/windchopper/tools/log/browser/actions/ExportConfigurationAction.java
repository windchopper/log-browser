package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ExportConfigurationAction extends AppAction {

    public ExportConfigurationAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.exportConfiguration"));
        graphicProperty().set(Pipeliner.of(ImageView::new)
            .set(view -> view::setImage, new Image("/com/github/windchopper/tools/log/browser/images/export-16.png"))
            .get());

        setHandler(event -> {

        });
    }

}
