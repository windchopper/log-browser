package com.github.windchopper.tools.log.browser.fs;

public class SftpFile extends RemoteFile {

    private final String path;
    private final boolean directory;

    SftpFile(String path, boolean directory) {
        this.path = path;
        this.directory = directory;
    }

    @Override public String path() {
        return path;
    }

    @Override public boolean directory() {
        return directory;
    }

}
