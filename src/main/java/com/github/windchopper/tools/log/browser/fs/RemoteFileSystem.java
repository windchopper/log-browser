package com.github.windchopper.tools.log.browser.fs;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public abstract class RemoteFileSystem implements AutoCloseable {

    @Override public abstract void close() throws IOException;

    public abstract RemoteFile root() throws IOException;
    public abstract List<RemoteFile> children(String path) throws IOException;

    public String normalizePath(String path) {
        String normalizedPath = StringUtils.endsWith(path, "..")
            ? StringUtils.substringBeforeLast(StringUtils.substringBeforeLast(path, "/"), "/")
            : path;

        if (StringUtils.isBlank(normalizedPath)) {
            normalizedPath = "/";
        }

        return normalizedPath;
    }

}
