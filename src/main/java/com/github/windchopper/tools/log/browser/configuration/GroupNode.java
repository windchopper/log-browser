package com.github.windchopper.tools.log.browser.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "grouping")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupNode extends ConfigurationNode implements ContainerNode {

    @XmlElement(name = "connection")
    private List<ConnectionNode> connections;
    @XmlElement(name = "group")
    private List<GroupNode> groups;

    @Override
    public List<ConnectionNode> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionNode> connections) {
        this.connections = connections;
    }

    @Override
    public List<GroupNode> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupNode> groups) {
        this.groups = groups;
    }

}
