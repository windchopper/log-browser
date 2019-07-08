package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.tools.log.browser.Globals;
import com.github.windchopper.tools.log.browser.fs.RemoteFileSystem;
import com.github.windchopper.tools.log.browser.fs.SftpFileSystem;

import java.io.IOException;

public enum ConnectionType {

    SECURE_FILE_TRANSFER(22, "/",
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.description")) {

        @Override public RemoteFileSystem newFileSystem(String host, int port, String username, String password) {
            return new SftpFileSystem(host, port, username, password);
        }

    };

    private final int defaultPort;
    private final String defaultPath;
    private final String title;
    private final String description;

    ConnectionType(int defaultPort, String defaultPath, String title, String description) {
        this.defaultPort = defaultPort;
        this.defaultPath = defaultPath;
        this.title = title;
        this.description = description;
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

    public String defaultPath() {
        return defaultPath;
    }

    public String displayName() {
        return String.format("%s (%s)", description, title);
    }

    public abstract RemoteFileSystem newFileSystem(String host, int port, String username, String password)
        throws IOException;

}
