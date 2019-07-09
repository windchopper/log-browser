package com.github.windchopper.tools.log.browser.events;

import java.util.Collection;

public class ConfirmPaths {

    private final Collection<String> paths;

    public ConfirmPaths(Collection<String> paths) {
        this.paths = paths;
    }

    public Collection<String> paths() {
        return paths;
    }

}
