package com.github.windchopper.tools.log.browser;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Cursor;
import javafx.stage.Stage;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped @Named("AsyncRunner") public class AsyncRunner {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PreDestroy void destroying() {
        executor.shutdown();
    }

    void runAsync(Stage stage, Collection<BooleanProperty> actionDisableProperties, Runnable action) {
        executor.execute(() -> {
            stage.getScene().setCursor(Cursor.WAIT);
            actionDisableProperties.forEach(property -> property.set(true));

            try {
                action.run();
            } finally {
                actionDisableProperties.forEach(property -> property.set(false));
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

}
