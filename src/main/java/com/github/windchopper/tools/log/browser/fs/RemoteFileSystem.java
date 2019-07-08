package com.github.windchopper.tools.log.browser.fs;

import java.io.IOException;
import java.util.List;

public abstract class RemoteFileSystem implements AutoCloseable {

    @Override public abstract void close() throws IOException;

    public abstract RemoteFile root() throws IOException;
    public abstract List<RemoteFile> children(RemoteFile file) throws IOException;

}
