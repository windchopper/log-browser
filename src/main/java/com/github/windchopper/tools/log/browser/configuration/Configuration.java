package com.github.windchopper.tools.log.browser.configuration;

import com.github.windchopper.tools.log.browser.crypto.Salt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "configuration") @XmlAccessorType(XmlAccessType.FIELD) public class Configuration extends GroupNode {

    @XmlAttribute(name = "salt") @XmlJavaTypeAdapter(Salt.XmlJavaTypeAdapter.class) private Salt salt;

    public Salt getSalt() {
        return salt;
    }

    public void setSalt(Salt salt) {
        this.salt = salt;
    }

}
