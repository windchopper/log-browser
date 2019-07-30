package com.github.windchopper.tools.log.browser.events;

import java.util.List;

public class PathListConfirm {

    private final List<String> paths;

    public PathListConfirm(List<String> paths) {
        this.paths = paths;
    }

    public List<String> paths() {
        return paths;
    }

}
