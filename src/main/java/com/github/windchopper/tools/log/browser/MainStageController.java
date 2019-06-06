package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.fx.annotation.FXMLResource;
import com.github.windchopper.common.fx.event.FXMLResourceOpen;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped @FXMLResource(FXMLResources.FXML__MAIN) public class MainStageController extends AnyStageController {

    @Inject protected Event<FXMLResourceOpen> fxmlFormOpenEvent;

}
