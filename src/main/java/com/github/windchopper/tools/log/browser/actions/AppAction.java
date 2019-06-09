package com.github.windchopper.tools.log.browser.actions;

import com.github.windchopper.common.fx.Action;
import javafx.scene.control.MenuItem;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppAction extends Action {

    static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    AppAction() {
        setExecutor(executor);
    }

    public static void shutdownExecutor() {
        executor.shutdown();
    }

    public MenuItem getBindMenuItem() {
        throw new UnsupportedOperationException();
    }

    public void setBindMenuItem(MenuItem menuItem) {
        bind(menuItem);
    }

}
