package com.github.windchopper.tools.log.browser.configuration;

import com.github.robtimus.filesystems.ftp.FTPFileSystemProvider;
import com.github.robtimus.filesystems.ftp.FTPSFileSystemProvider;
import com.github.robtimus.filesystems.sftp.SFTPFileSystemProvider;
import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.Globals;
import com.pastdev.jsch.DefaultSessionFactory;
import com.pastdev.jsch.nio.file.UnixSshFileSystemProvider;
import com.pastdev.jsch.nio.file.UnixSshSftpHybridFileSystemProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Properties;

public enum ConnectionType {

    SECURE_SHELL(
        UnixSshFileSystemProvider.class,
        (username, password, host, port) -> Map.of("defaultSessionFactory", Pipeliner.of(DefaultSessionFactory::new)
            .set(factory -> factory::setUsername, username)
            .set(factory -> factory::setPassword, password)
            .accept(factory -> factory.setConfig("StrictHostKeyChecking", "no"))
            .get()),
        UnixSshFileSystemProvider.SCHEME_SSH_UNIX,
        22,
        "/",
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShell.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShell.description")),
    SECURE_SHELL_AND_FILE_TRANSFER(
        UnixSshSftpHybridFileSystemProvider.class,
        (username, password, host, port) -> Map.of("defaultSessionFactory", Pipeliner.of(DefaultSessionFactory::new)
            .set(factory -> factory::setUsername, username)
            .set(factory -> factory::setPassword, password)
            .accept(factory -> factory.setConfig("StrictHostKeyChecking", "no"))
            .get()),
        UnixSshSftpHybridFileSystemProvider.SCHEME_SSH_SFTP_HYBRID_UNIX,
        22,
        "/",
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShellWithSecureFileTransferFallback.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureShellWithSecureFileTransferFallback.description")),
    SECURE_FILE_TRANSFER(
        SFTPFileSystemProvider.class,
        (username, password, host, port) -> Map.of(
            "username", username,
            "password", password.toCharArray(),
            "config", Pipeliner.of(Properties::new)
                .accept(properties -> properties.setProperty("StrictHostKeyChecking", "no"))
                .get()),
        "sftp",
        22,
        null,
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.description")),
    FILE_TRANSFER(FTPFileSystemProvider.class,
        (username, password, host, port) -> Map.of("username", username, "password", password.toCharArray()),
        "ftp",
        21,
        null,
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransfer.description")),
    FILE_TRANSFER_SECURE_SOCKET_LAYER(
        FTPSFileSystemProvider.class,
        (username, password, host, port) -> Map.of("username", username, "password", password.toCharArray()),
        "ftps",
        989,
        null,
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransferOverSecureSocketLayer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.fileTransferOverSecureSocketLayer.description"));

    private final Class<? extends FileSystemProvider> providerType;
    private final EnvironmentBuilder environmentBuilder;
    private final String scheme;
    private final int defaultPort;
    private final String defaultPath;
    private final String title;
    private final String description;

    private FileSystemProvider provider;

    @FunctionalInterface public interface EnvironmentBuilder {

        Map<String, ?> buildEnvironment(String username, String password, String host, int port);

    }

    ConnectionType(Class<? extends FileSystemProvider> providerType, EnvironmentBuilder environmentBuilder, String scheme, int defaultPort, String defaultPath, String title, String description) {
        this.providerType = providerType;
        this.scheme = scheme;
        this.environmentBuilder = environmentBuilder;
        this.defaultPort = defaultPort;
        this.defaultPath = defaultPath;
        this.title = title;
        this.description = description;
    }

    public Class<? extends FileSystemProvider> providerType() {
        return providerType;
    }

    public String scheme() {
        return scheme;
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

    public String displayName() {
        return String.format("%s (%s)", description, title);
    }

    public FileSystem fileSystem(String username, String password, String host, int port) throws IOException {
        try {
            if (provider == null) {
                provider = providerType.getConstructor().newInstance();
            }

            URI uri = new URI(scheme, null, host, port, defaultPath, null, null);

            try {
                return provider.getFileSystem(uri);
            } catch (FileSystemNotFoundException thrown) {
                return provider.newFileSystem(uri, environmentBuilder.buildEnvironment(username, password, host, port));
            }
        } catch (ReflectiveOperationException | URISyntaxException thrown) {
            throw new IOException(thrown);
        }
    }

}
