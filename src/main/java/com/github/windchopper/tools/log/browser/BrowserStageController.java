package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import javafx.stage.Stage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.net.URI;
import java.util.Map;

@ApplicationScoped @FXMLResource(Globals.FXML__BROWSER) @Named("BrowserStageController") public class BrowserStageController extends BaseStageController {

    private URI uri;

    @Override protected void start(Stage stage, String fxmlResource, Map<String, ?> parameters, Map<String, ?> fxmlLoaderNamespace) {
        super.start(stage, fxmlResource, parameters, fxmlLoaderNamespace);

        uri = (URI) parameters.get("uri");
    }

}
