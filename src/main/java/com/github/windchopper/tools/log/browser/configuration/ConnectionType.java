package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.tools.log.browser.Globals;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public enum ConnectionType {

    SECURE_SHELL("ssh.unix", 22,
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShell.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShell.description")),
    SECURE_FILE_TRANSFER("sftp", 22,
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.description")),
    FILE_TRANSFER("ftp", 21,
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransfer.description"));

    private final String uriScheme;
    private final int defaultPort;
    private final String title;
    private final String description;

    ConnectionType(String uriScheme, int defaultPort, String title, String description) {
        this.uriScheme = uriScheme;
        this.defaultPort = defaultPort;
        this.title = title;
        this.description = description;
    }

    public String uriScheme() {
        return uriScheme;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public int defaultPort() {
        return defaultPort;
    }

}
