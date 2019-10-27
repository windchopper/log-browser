package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.cdi.ResourceBundleLoad;
import com.github.windchopper.common.fx.cdi.form.FormLoader;
import com.github.windchopper.common.fx.cdi.form.StageFormLoad;
import com.github.windchopper.common.util.ClassPathResource;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Launcher extends Application {

    private Weld weld;
    private WeldContainer weldContainer;

    @Override public void init() throws Exception {
        super.init();
        weld = new Weld()
            .enableDiscovery()
            .addPackages(FormLoader.class);
        weldContainer = weld.initialize();
    }

    @Override public void stop() throws Exception {
        weld.shutdown();
        super.stop();
    }

    @Override public void start(Stage primaryStage) {
        var beanManager = weldContainer.getBeanManager();

        beanManager.fireEvent(
            new ResourceBundleLoad(Globals.bundle));
        beanManager.fireEvent(
            new StageFormLoad(
                new ClassPathResource(Globals.FXML__MAIN),
                () -> primaryStage));
    }

    /*
     *
     */

    public static void main(String... args) {
        launch(args);
    }

}
