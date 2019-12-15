package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.util.Pipeliner;
import com.github.windchopper.tools.log.browser.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped @Named("ConfigurationAccess") public class ConfigurationAccess {

    private static final Logger logger = Logger.getLogger(ConfigurationAccess.class.getName());

    private Marshaller marshaller;

    private Path configurationFile;
    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    @PostConstruct private void afterConstruction() {
        try {
            configurationFile = Paths.get(Optional.ofNullable(System.getProperty("user.home"))
                .orElse(""))
                .resolve(".log-browser")
                .resolve("configuration.xml");

            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);

                marshaller = Pipeliner.of(jaxbContext)
                    .mapFailable(JAXBContext::createMarshaller)
                    .acceptFailable(marshaller -> marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true))
                    .get();

                try (Reader reader = Files.newBufferedReader(configurationFile)) {
                    configuration = (Configuration) jaxbContext
                        .createUnmarshaller()
                        .unmarshal(reader);
                }
            } catch (IOException thrown) {
                logger.log(Level.WARNING, ExceptionUtils.getRootCauseMessage(thrown), thrown);
                configuration = new Configuration();
            }

            if (StringUtils.isBlank(configuration.getName())) {
                configuration.setName(Globals.bundle.getString("com.github.windchopper.tools.log.browser.main.newConfiguration"));
            }
        } catch (Exception thrown) {
            throw new IllegalStateException(thrown);
        }
    }

    void saveConfiguration() throws IOException, JAXBException {
        Path tempFile = Files.createTempFile("save-conf-temp-", ".xml");

        try (Writer writer = Files.newBufferedWriter(tempFile)) {
            marshaller.marshal(configuration, writer);
        }

        Files.move(
            tempFile,
            Files.createDirectories(configurationFile.getParent()).resolve(configurationFile.getFileName()),
            StandardCopyOption.REPLACE_EXISTING);
    }

}
