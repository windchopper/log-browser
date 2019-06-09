package com.github.windchopper.tools.log.browser.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "connection") @XmlAccessorType(XmlAccessType.FIELD) public class Connection extends ConfigurationElement {

    @XmlElement(name = "type") private ConnectionType type;
    @XmlElement(name = "host") private String host;
    @XmlElement(name = "username") private String username;
    @XmlElement(name = "password") private String password;

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

}
