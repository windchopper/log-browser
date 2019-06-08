package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.fx.Action;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppAction extends Action {

    static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AppAction() {
        setExecutor(executor);
    }

    public static void shutdownExecutor() {
        executor.shutdown();
    }

}
