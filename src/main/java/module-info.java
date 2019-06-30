module windchopper.tools.log.browser {

    opens com.github.windchopper.tools.log.browser;
    opens com.github.windchopper.tools.log.browser.configuration;
    opens com.github.windchopper.tools.log.browser.preferences;
    opens com.github.windchopper.tools.log.browser.i18n;
    opens com.github.windchopper.tools.log.browser.images;

    requires java.annotation;
    requires java.logging;
    requires java.prefs;
    requires java.xml.bind;

    requires javax.inject;

    requires javafx.controls;
    requires javafx.fxml;

    requires cdi.api;

    requires weld.se.core;
    requires weld.environment.common;
    requires weld.core.impl;

    requires windchopper.common;
    requires org.apache.commons.lang3;
    requires jsch;
    requires jsch.nio;
    requires jsch.extension;
    requires sftp.fs;
    requires ftp.fs;

    uses com.pastdev.jsch.nio.file.UnixSshFileSystemProvider;
    uses com.pastdev.jsch.nio.file.UnixSshSftpHybridFileSystemProvider;
    uses com.github.robtimus.filesystems.sftp.SFTPFileSystemProvider;
    uses com.github.robtimus.filesystems.ftp.FTPFileSystemProvider;
    uses com.github.robtimus.filesystems.ftp.FTPSFileSystemProvider;

}