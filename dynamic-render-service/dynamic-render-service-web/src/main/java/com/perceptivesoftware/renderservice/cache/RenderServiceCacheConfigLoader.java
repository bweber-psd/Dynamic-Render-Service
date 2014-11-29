package com.perceptivesoftware.renderservice.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.saperion.cache.config.AbstractCacheConfigLoader;
import com.saperion.cache.config.ConfigFileHolder;
import com.saperion.cache.config.xml.CacheType;
import com.saperion.cache.config.xml.CachesType;
import com.saperion.cache.util.configuration.ConfigurationUtil;
import com.saperion.common.logging.Logger;

/**
 * Loads the configuration file for the cache.
 */
public class RenderServiceCacheConfigLoader extends AbstractCacheConfigLoader {

	private static final String CONFIG_FILE = "/config/sacache.xml";

	private static final Logger LOGGER = Logger.getLogger(RenderServiceCacheConfigLoader.class);

	private final String applicationIdentifier;

	private Unmarshaller unmarshaller;

	public RenderServiceCacheConfigLoader(String applicationIdentifier) {
		this.applicationIdentifier = applicationIdentifier;
	}

	@Override
	public void initialize() {
		try {
			initializeUnmarshaller();
			caches = loadConfigurationFile(CONFIG_FILE);

			for (CacheType cache : caches.getCache()) {
				cache.setJmxCategoryName(applicationIdentifier);
			}

		} catch (JAXBException | IOException | SAXException e) {
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
		}
	}

	private void initializeUnmarshaller() throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(CachesType.class);
		unmarshaller = jc.createUnmarshaller();
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		String path = ConfigurationUtil.getPath(ConfigFileHolder.CACHES);
		URL xsdURL = RenderServiceCacheConfigLoader.class.getResource(path);
		Schema schema = sf.newSchema(xsdURL);
		unmarshaller.setSchema(schema);
	}

	private CachesType loadConfigurationFile(String filename) throws IOException, JAXBException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Trying to read configuration file " + filename);
		}

		try (InputStream xmlStream =
				RenderServiceCacheConfigLoader.class.getResourceAsStream(filename)) {
			return (CachesType) unmarshaller.unmarshal(xmlStream);
		}
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

}
