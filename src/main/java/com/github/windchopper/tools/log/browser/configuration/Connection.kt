package com.github.windchopper.tools.log.browser.configuration

import javax.xml.bind.annotation.*

@XmlType(name = "connection") @XmlAccessorType(XmlAccessType.FIELD) class Connection: ConfigurationElement() {

    @XmlElement(name = "type") var type: ConnectionType? = null
    @XmlElement(name = "host") var host: String? = null
    @XmlElement(name = "port") var port: Int? = null
    @XmlElement(name = "username") var username: String? = null
    @XmlElement(name = "password") var password: String? = null
    @XmlElementWrapper(name = "paths") @XmlElement(name = "path") var pathList: MutableList<String> = ArrayList()

}