package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Pipeliner;

import java.util.List;

public interface ContainerNode {

    List<ConnectionNode> getConnections();

    default ConnectionNode addConnection() {
        return Pipeliner.of(ConnectionNode::new)
            .accept(getConnections()::add)
            .get();
    }

    List<GroupNode> getGroups();

    default GroupNode addGroup() {
        return Pipeliner.of(GroupNode::new)
            .accept(getGroups()::add)
            .get();
    }

}
