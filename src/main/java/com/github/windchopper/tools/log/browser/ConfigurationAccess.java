package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.util.KnownSystemProperties;
import com.github.windchopper.tools.log.browser.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

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
import java.util.ResourceBundle;

@ApplicationScoped @Named("ConfigurationAccess") public class ConfigurationAccess {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    private Marshaller marshaller;

    private Path configurationFile;
    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    @PostConstruct private void afterConstruction() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);

            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            configurationFile = KnownSystemProperties.userHomePath.get()
                .orElseGet(() -> Paths.get(""))
                .resolve(".log-browser/configuration.xml");

            try (Reader reader = Files.newBufferedReader(configurationFile)) {
                configuration = (Configuration) jaxbContext
                    .createUnmarshaller()
                    .unmarshal(reader);
            }

            if (StringUtils.isBlank(configuration.getName())) {
                configuration.setName(bundle.getString("com.github.windchopper.tools.log.browser.main.newConfiguration"));
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
