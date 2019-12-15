package com.github.windchopper.tools.log.browser.configuration

import com.github.windchopper.fs.sftp.SftpConstants
import com.github.windchopper.tools.log.browser.Globals
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.FileSystem
import java.nio.file.FileSystems

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