package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.preferences.PlatformPreferencesStorage;
import com.github.windchopper.common.preferences.PreferencesStorage;

import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public interface Globals {

    String FXML__MAIN = "com/github/windchopper/tools/log/browser/main.fxml";
    String FXML__CONNECTION = "com/github/windchopper/tools/log/browser/connection.fxml";
    String FXML__GROUP = "com/github/windchopper/tools/log/browser/group.fxml";
    String FXML__GATHERING = "com/github/windchopper/tools/log/browser/gathering.fxml";

    ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    PreferencesStorage preferencesStorage = new PlatformPreferencesStorage(Preferences.userRoot()
        .node("com/github/windchopper/tools/log/browser"));

}
