package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Pipeliner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType(name = "group") @XmlAccessorType(XmlAccessType.FIELD) public class GroupNode extends ConfigurationNode {

    @XmlElement(name = "connection") private List<ConnectionNode> connections;
    @XmlElement(name = "group") private List<GroupNode> groups;

    public List<ConnectionNode> getConnections() {
        if (connections == null) {
            connections = new ArrayList<>();
        }

        return connections;
    }

    public void setConnections(List<ConnectionNode> connections) {
        this.connections = connections;
    }

    public ConnectionNode addConnection() {
        return Pipeliner.of(ConnectionNode::new)
            .accept(getConnections()::add)
            .get();
    }

    public List<GroupNode> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }

        return groups;
    }

    public void setGroups(List<GroupNode> groups) {
        this.groups = groups;
    }

    public GroupNode addGroup() {
        return Pipeliner.of(GroupNode::new)
            .accept(getGroups()::add)
            .get();
    }

}
