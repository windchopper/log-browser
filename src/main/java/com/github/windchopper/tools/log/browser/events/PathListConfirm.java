package com.github.windchopper.tools.log.browser.events;

import java.util.Collection;

public class PathListConfirm {

    private final Collection<String> paths;

    public PathListConfirm(Collection<String> paths) {
        this.paths = paths;
    }

    public Collection<String> paths() {
        return paths;
    }

}
