package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.fs.sftp.SftpConstants;
import com.github.windchopper.tools.log.browser.Globals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Map;

public enum ConnectionType {

    SECURE_FILE_TRANSFER(22, "/",
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.description")) {

        @Override public FileSystem newFileSystem(String host, int port, String username, String password) throws IOException {
            try {
                return FileSystems.newFileSystem(new URI(SftpConstants.SCHEME, null, host, port, null, null, null),
                    Map.of(SftpConstants.USERNAME, username, SftpConstants.PASSWORD, password));
            } catch (URISyntaxException thrown) {
                throw new IOException(thrown.getMessage(), thrown);
            }
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

    public abstract FileSystem newFileSystem(String host, int port, String username, String password)
        throws IOException;

}
