package com.github.windchopper.tools.log.browser.configuration

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlType

@XmlType @XmlAccessorType(XmlAccessType.FIELD) abstract class ConfigurationElement {

    @XmlAttribute(name = "name") var name: String? = null

}