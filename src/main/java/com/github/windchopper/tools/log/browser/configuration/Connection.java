package com.github.windchopper.tools.log.browser.configuration;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlType(name = "connection") @XmlAccessorType(XmlAccessType.FIELD) public class Connection extends ConfigurationElement {

    @XmlElement(name = "type") private ConnectionType type;
    @XmlElement(name = "host") private String host;
    @XmlElement(name = "port") private int port;
    @XmlElement(name = "username") private String username;
    @XmlElement(name = "password") private String password;
    @XmlElementWrapper(name = "paths") @XmlElement(name = "path") private List<String> pathList;

    public ConnectionType getType() {
        return type;
    }

    public void setType(ConnectionType type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

}
