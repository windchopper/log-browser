package com.github.windchopper.tools.log.browser;

import com.github.windchopper.common.util.BufferedReference;
import com.github.windchopper.tools.log.browser.configuration.ConfigurationRoot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public interface XmlAware {

    BufferedReference<JAXBContext, JAXBException> jaxbContextReference = new BufferedReference<>(() -> JAXBContext.newInstance(ConfigurationRoot.class));

    default ConfigurationRoot loadConfiguration(Path configurationFile) throws IOException, JAXBException {
        try (Reader reader = Files.newBufferedReader(configurationFile)) {
            return (ConfigurationRoot) jaxbContextReference.get()
                .createUnmarshaller()
                .unmarshal(reader);
        }
    }

}
