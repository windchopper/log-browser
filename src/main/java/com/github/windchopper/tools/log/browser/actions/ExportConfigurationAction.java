package com.github.windchopper.tools.log.browser.actions;

public class ExportConfigurationAction extends AppAction {

    public ExportConfigurationAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.exportConfiguration"));
        setHandler(event -> {

        });
    }

}
