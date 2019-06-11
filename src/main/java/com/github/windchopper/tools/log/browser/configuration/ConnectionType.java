package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.tools.log.browser.Globals;

public enum ConnectionType {

    SECURE_SHELL(
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShell.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShell.description")),
    SECURE_FILE_TRANSFER(
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransfer.description")),
    FILE_TRANSFER(
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.description"));

    private final String title;
    private final String description;

    ConnectionType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

}
