package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.common.util.Pipeliner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType(name = "group") @XmlAccessorType(XmlAccessType.FIELD) public class Group extends ConfigurationElement {

    @XmlElement(name = "connection") private List<Connection> connections;
    @XmlElement(name = "group") private List<Group> groups;

    public List<Connection> getConnections() {
        if (connections == null) {
            connections = new ArrayList<>();
        }

        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public Connection addConnection() {
        return Pipeliner.of(Connection::new)
            .accept(getConnections()::add)
            .get();
    }

    public List<Group> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }

        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Group addGroup() {
        return Pipeliner.of(Group::new)
            .accept(getGroups()::add)
            .get();
    }

}
