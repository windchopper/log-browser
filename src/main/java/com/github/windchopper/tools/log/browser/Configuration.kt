package com.github.windchopper.tools.log.browser

import com.github.windchopper.fs.sftp.SftpConstants
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import javax.xml.bind.annotation.*

@XmlRootElement(name = "configuration") @XmlAccessorType(XmlAccessType.FIELD) class Configuration: Group()

@XmlType @XmlAccessorType(XmlAccessType.FIELD) abstract class ConfigurationElement {

    @XmlAttribute(name = "name") var name: String? = null

}

@XmlType(name = "connection") @XmlAccessorType(XmlAccessType.FIELD) class Connection: ConfigurationElement() {

    @XmlElement(name = "type") var type: ConnectionType? = null
    @XmlElement(name = "host") var host: String? = null
    @XmlElement(name = "port") var port: Int? = null
    @XmlElement(name = "username") var username: String? = null
    @XmlElement(name = "password") var password: String? = null
    @XmlElementWrapper(name = "paths") @XmlElement(name = "path") var pathList: MutableList<String> = ArrayList()

}

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

enum class ConnectionType(val defaultPort: Int, val defaultPath: String, val title: String, val description: String, val displayName: String = "${description} (${title})") {

    SECURE_FILE_TRANSFER(22, "/",

        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.title"),
        Globals.bundle.getString("com.github.windchopper.tools.log.browser.secureFileTransfer.description")) {

        @Throws(IOException::class) override fun newFileSystem(host: String, port: Int, username: String?, password: String?): FileSystem {
            return try {
                FileSystems.newFileSystem(URI(SftpConstants.SCHEME, null, host, port, null, null, null),
                    mapOf(SftpConstants.USERNAME to username, SftpConstants.PASSWORD to password))
            } catch (thrown: URISyntaxException) {
                throw IOException(thrown.message, thrown)
            }
        }

    };

    @Throws(IOException::class) abstract fun newFileSystem(host: String, port: Int, username: String?, password: String?): FileSystem

}