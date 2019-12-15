module windchopper.tools.log.browser {

    opens com.github.windchopper.tools.log.browser;
    opens com.github.windchopper.tools.log.browser.configuration;
    opens com.github.windchopper.tools.log.browser.preferences;
    opens com.github.windchopper.tools.log.browser.i18n;
    opens com.github.windchopper.tools.log.browser.images;

    requires java.logging;
    requires java.prefs;
    requires java.annotation;
    requires java.xml.bind;
    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires kotlinx.coroutines.core;
    requires kotlinx.coroutines.javafx;
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.inject.api;
    requires jakarta.enterprise.cdi.api;
    requires weld.se.core;
    requires weld.environment.common;
    requires weld.core.impl;
    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires windchopper.fs;
    requires windchopper.common.fx;
    requires windchopper.common.fx.cdi;
    requires windchopper.common.preferences;
    requires windchopper.common.util;

}