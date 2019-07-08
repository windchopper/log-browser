package com.github.windchopper.tools.log.browser.events;

import java.util.List;

public class ConfirmPathList {

    private final List<String> paths;

    public ConfirmPathList(List<String> paths) {
        this.paths = paths;
    }

    public List<String> paths() {
        return paths;
    }

}
