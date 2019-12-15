package com.github.windchopper.tools.log.browser.configuration

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlType(name = "group") @XmlAccessorType(XmlAccessType.FIELD) open class Group: ConfigurationElement() {

    @XmlElement(name = "connection") var connections: MutableList<Connection> = ArrayList()
    @XmlElement(name = "group") var groups: MutableList<Group> = ArrayList()

    fun addConnection(): Connection {
        return Connection().also {
            connections.add(it)
        }
    }

    fun addGroup(): Group {
        return Group().also {
            groups.add(it)
        }
    }

}