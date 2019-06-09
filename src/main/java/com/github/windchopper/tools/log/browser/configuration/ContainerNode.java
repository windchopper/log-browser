package com.github.windchopper.tools.log.browser.configuration;

import java.util.List;

public interface ContainerNode {

    List<ConnectionNode> getConnections();
    List<GroupNode> getGroups();

}
