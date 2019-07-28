package com.github.windchopper.tools.log.browser;

import javafx.application.Platform;

public interface FormControllerRoutines {

    default void runWithFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

}
