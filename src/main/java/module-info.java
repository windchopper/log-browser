module windchopper.tools.log.browser {

    opens com.github.windchopper.tools.log.browser;
    opens com.github.windchopper.tools.log.browser.configuration;
    opens com.github.windchopper.tools.log.browser.i18n;
    opens com.github.windchopper.tools.log.browser.images;

    requires java.annotation;
    requires java.logging;
    requires java.prefs;
    requires java.xml.bind;

    requires javax.inject;

    requires javafx.controls;
    requires javafx.fxml;

    requires cdi.api;

    requires weld.se.core;
    requires weld.environment.common;
    requires weld.core.impl;

    requires org.apache.commons.lang3;
    requires jsch;

    requires windchopper.common;

}