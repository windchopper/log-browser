package com.github.windchopper.tools.log.browser.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ssh-connection") @XmlAccessorType(XmlAccessType.FIELD) public class SecureShellConnectionNode extends ConnectionNode {

    @XmlElement(name = "host") private String host;
    @XmlElement(name = "username") private String username;
    @XmlElement(name = "password") private String password;

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
