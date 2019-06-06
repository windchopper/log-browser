module windchopper.tools.log.browser {

    opens com.github.windchopper.tools.log.browser;

    requires javax.inject;

    requires javafx.controls;
    requires javafx.fxml;

    requires cdi.api;

    requires weld.se.core;
    requires weld.environment.common;
    requires weld.core.impl;

    requires windchopper.common;

}