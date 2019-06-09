package com.github.windchopper.tools.log.browser.actions;

public class UngroupAction extends ConfigurationTreeAction {

    public UngroupAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.ungroup"));
        setHandler(event -> {
            
        });
    }

    @Override public void prepare() {

    }

}
