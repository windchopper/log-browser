package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.preferences.PlatformPreferencesStorage;
import com.github.windchopper.common.preferences.PreferencesEntry;
import com.github.windchopper.common.preferences.PreferencesStorage;
import com.github.windchopper.common.preferences.types.FlatType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.File;
import java.time.Duration;
import java.util.prefs.Preferences;

import static java.util.function.Function.identity;

@ApplicationScoped @Named("PreferencesAccess") public class PreferencesAccess {

    Duration defaultBufferLifetime = Duration.ofMinutes(1);
    PreferencesStorage preferencesStorage = new PlatformPreferencesStorage(Preferences.userRoot().node("com/github/windchopper/tools/log/browser"));

//    String PREFERENCES_ENTRY_NAME__FILTER_TEXT = "filterText";
//    String PREFERENCES_ENTRY_NAME__BROWSE_INITIAL_DIRECTORY = "browseInitialDirectory";
//    String PREFERENCES_ENTRY_NAME__AUTO_REFRESH = "autoRefresh";
//
//    PreferencesEntry<String> filterTextPreferencesEntry = new PreferencesEntry<>(
//        preferencesStorage, PREFERENCES_ENTRY_NAME__FILTER_TEXT, new FlatType<>(identity(), identity()), defaultBufferLifetime);
//
//    PreferencesEntry<File> browseInitialDirectoryPreferencesEntry = new PreferencesEntry<>(
//        preferencesStorage, PREFERENCES_ENTRY_NAME__BROWSE_INITIAL_DIRECTORY, new FlatType<>(File::new, File::getAbsolutePath), defaultBufferLifetime);
//
//    PreferencesEntry<Boolean> autoRefreshPreferencesEntry = new PreferencesEntry<>(
//        preferencesStorage, PREFERENCES_ENTRY_NAME__AUTO_REFRESH, new FlatType<>(Boolean::parseBoolean, Object::toString), defaultBufferLifetime);

}
