package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.util.Pipeliner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ExportConfigurationAction extends AppAction {

    public ExportConfigurationAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.exportConfiguration"));
        setHandler(event -> {

        });
    }

}
