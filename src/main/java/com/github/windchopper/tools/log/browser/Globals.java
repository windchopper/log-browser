package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.preferences.PlatformPreferencesStorage;
import com.github.windchopper.common.preferences.PreferencesStorage;

import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public interface Globals {

    String FXML__MAIN = "com/github/windchopper/tools/log/browser/main.fxml";
    String FXML__PASSWORD = "com/github/windchopper/tools/log/browser/password.fxml";
    String FXML__PREFERENCES = "com/github/windchopper/tools/log/browser/preferences.fxml";

    ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    PreferencesStorage preferencesStorage = new PlatformPreferencesStorage(Preferences.userRoot()
        .node("com/github/windchopper/tools/log/browser"));

}
