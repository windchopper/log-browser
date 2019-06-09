package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.event.FXMLResourceOpen;
import com.github.windchopper.common.fx.event.ResourceBundleLoading;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Launcher extends Application {

    private Weld weld;
    private WeldContainer weldContainer;

    @Override public void init() throws Exception {
        super.init();
        weld = new Weld();
        weldContainer = weld.initialize();
    }

    @Override public void stop() throws Exception {
        weld.shutdown();
        super.stop();
    }

    @Override public void start(Stage primaryStage) {
        var beanManager = weldContainer.getBeanManager();
        beanManager.fireEvent(
            new ResourceBundleLoading(Globals.bundle));
        beanManager.fireEvent(
            new FXMLResourceOpen(primaryStage, Globals.FXML__MAIN));
    }

    /*
     *
     */

    public static void main(String... args) {
        launch(args);
    }

}
