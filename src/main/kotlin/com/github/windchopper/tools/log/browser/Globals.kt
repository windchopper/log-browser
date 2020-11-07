package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.preferences.PlatformPreferencesStorage
import com.github.windchopper.common.preferences.PreferencesStorage
import javafx.scene.image.Image
import java.util.*
import java.util.prefs.Preferences

object Globals {

    const val FXML__MAIN = "com/github/windchopper/tools/log/browser/main.fxml"
    const val FXML__CONNECTION = "com/github/windchopper/tools/log/browser/connection.fxml"
    const val FXML__BROWSE = "com/github/windchopper/tools/log/browser/browse.fxml"
    const val FXML__GROUP = "com/github/windchopper/tools/log/browser/group.fxml"
    const val FXML__DOWNLOAD = "com/github/windchopper/tools/log/browser/download.fxml"

    val logoImage = Image("/com/github/windchopper/tools/log/browser/images/scroll-48.png")
    val bundle: ResourceBundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages")
    val preferencesStorage: PreferencesStorage = PlatformPreferencesStorage(Preferences.userRoot()
        .node("com/github/windchopper/tools/log/browser"))

}