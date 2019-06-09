package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.util.KnownSystemProperties;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationRoot;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.logging.Logger;

@ApplicationScoped @Named("ConfigurationAccess") public class ConfigurationAccess {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.github.windchopper.tools.log.browser.i18n.messages");

    private JAXBContext jaxbContext;

    private Path configurationFile;
    private ConfigurationRoot configuration;

    public ConfigurationRoot getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationRoot configuration) {
        this.configuration = configuration;
    }

    @PostConstruct private void afterConstruction() throws JAXBException, IOException {
        jaxbContext = JAXBContext.newInstance(ConfigurationRoot.class);

        configurationFile = KnownSystemProperties.userHomePath.get()
            .orElseGet(() -> Paths.get(""))
            .resolve(".log-browser/configuration.xml");

        try (Reader reader = Files.newBufferedReader(configurationFile)) {
            configuration = (ConfigurationRoot) jaxbContext
                .createUnmarshaller()
                .unmarshal(reader);
        }

        if (StringUtils.isBlank(configuration.getName())) {
            configuration.setName(bundle.getString("com.github.windchopper.tools.log.browser.main.newConfiguration"));
        }
    }

    void saveConfiguration() throws IOException, JAXBException {
        Path tempFile = Files.createTempFile("save-conf-temp-", ".xml");

        try (Writer writer = Files.newBufferedWriter(tempFile)) {
            jaxbContext
                .createMarshaller()
                .marshal(configuration, writer);
        }

        Files.move(
            tempFile,
            Files.createDirectories(configurationFile.getParent()).resolve(configurationFile.getFileName()),
            StandardCopyOption.REPLACE_EXISTING);
    }

}
