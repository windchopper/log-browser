package com.github.windchopper.tools.log.browser.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType(name = "grouping") @XmlAccessorType(XmlAccessType.FIELD) public class GroupNode extends ConfigurationNode implements ContainerNode {

    @XmlElement(name = "connection") private List<SecureShellConnectionNode> secureShellConnections;
    @XmlElement(name = "group") private List<GroupNode> groups;

    @Override public List<SecureShellConnectionNode> getSecureShellConnections() {
        if (secureShellConnections == null) {
            secureShellConnections = new ArrayList<>();
        }

        return secureShellConnections;
    }

    public void setSecureShellConnections(List<SecureShellConnectionNode> secureShellConnections) {
        this.secureShellConnections = secureShellConnections;
    }

    @Override public List<GroupNode> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }

        return groups;
    }

    public void setGroups(List<GroupNode> groups) {
        this.groups = groups;
    }

}
