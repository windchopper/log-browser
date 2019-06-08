package com.github.windchopper.tools.log.browser.actions;

public class ImportConfigurationAction extends AppAction {

    public ImportConfigurationAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.importConfiguration"));
        setHandler(event -> {

        });
    }

}
