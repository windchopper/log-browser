package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Pipeliner;

import java.util.List;

public interface ContainerNode {

    List<SecureShellConnectionNode> getSecureShellConnections();

    default SecureShellConnectionNode addSecureShellConnection() {
        return Pipeliner.of(SecureShellConnectionNode::new)
            .accept(getSecureShellConnections()::add)
            .get();
    }

    List<GroupNode> getGroups();

    default GroupNode addGroup() {
        return Pipeliner.of(GroupNode::new)
            .accept(getGroups()::add)
            .get();
    }

}
