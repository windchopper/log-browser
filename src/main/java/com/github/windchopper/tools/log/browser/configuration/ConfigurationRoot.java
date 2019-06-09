package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.tools.log.browser.crypto.EncryptorSalt;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "configuration") @XmlAccessorType(XmlAccessType.FIELD) public class ConfigurationRoot extends ConfigurationNode implements ContainerNode {

    @XmlAttribute(name = "salt") @XmlJavaTypeAdapter(EncryptorSalt.XmlJavaTypeAdapter.class) private EncryptorSalt salt;
    @XmlElement(name = "ssh-connection") private List<SecureShellConnectionNode> secureShellConnections;
    @XmlElement(name = "group") private List<GroupNode> groups;

    public EncryptorSalt getSalt() {
        return salt;
    }

    public void setSalt(EncryptorSalt salt) {
        this.salt = salt;
    }

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
