package com.github.windchopper.tools.log.browser.actions;

public class GatherAction extends AppAction {

    public GatherAction() {
        textProperty().set(bundle.getString("com.github.windchopper.tools.log.browser.main.gather"));
        setHandler(event -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
