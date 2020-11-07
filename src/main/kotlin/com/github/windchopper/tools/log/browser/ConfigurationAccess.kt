package com.github.windchopper.tools.log.browser

import com.github.windchopper.common.util.Pipeliner
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.PropertyException
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@ApplicationScoped @Named("ConfigurationAccess") class ConfigurationAccess {

    private val logger = Logger.getLogger(this::class.qualifiedName)
    private var marshaller: Marshaller? = null
    private var configurationFile: Path? = null

    var configuration: Configuration? = null

    @PostConstruct private fun afterConstruction() {
        try {
            configurationFile = Paths.get(Optional.ofNullable(System.getProperty("user.home"))
                .orElse(""))
                .resolve(".log-browser")
                .resolve("configuration.xml")
            try {
                val jaxbContext = JAXBContext.newInstance(Configuration::class.java)
                marshaller = Pipeliner.of(jaxbContext)
                    .mapFailable<Marshaller, JAXBException> { obj: JAXBContext -> obj.createMarshaller() }
                    .acceptFailable<PropertyException> { marshaller: Marshaller -> marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true) }
                    .get()
                Files.newBufferedReader(configurationFile).use { reader ->
                    configuration = jaxbContext
                        .createUnmarshaller()
                        .unmarshal(reader) as Configuration
                }
            } catch (thrown: IOException) {
                logger.log(Level.WARNING, ExceptionUtils.getRootCauseMessage(thrown), thrown)
                configuration = Configuration()
            }
            if (StringUtils.isBlank(configuration!!.name)) {
                configuration!!.name = Globals.bundle.getString("com.github.windchopper.tools.log.browser.main.newConfiguration")
            }
        } catch (thrown: Exception) {
            throw IllegalStateException(thrown)
        }
    }

    @Throws(IOException::class, JAXBException::class) fun saveConfiguration() {
        val tempFile = Files.createTempFile("save-conf-temp-", ".xml")
        Files.newBufferedWriter(tempFile).use { writer -> marshaller!!.marshal(configuration, writer) }
        Files.move(
            tempFile,
            Files.createDirectories(configurationFile!!.parent).resolve(configurationFile!!.fileName),
            StandardCopyOption.REPLACE_EXISTING)
    }

}