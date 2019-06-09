package com.github.windchopper.tools.log.browser.actions;

public class RemoveAction extends ConfigurationTreeAction {

    public RemoveAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.tree.menu.remove"));
        setHandler(event -> {

        });
    }

    @Override public void prepare() {

    }

}
