package com.perceptivesoftware.renderservice.configuration;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.saperion.common.config.exception.ConfigurationException;
import com.saperion.common.config.loader.ChainedPropertiesLoader;
import com.saperion.common.config.loader.FilePropertiesLoader;
import com.saperion.common.config.loader.JndiPropertiesLoader;
import com.saperion.common.config.resources.PropertiesResource;
import com.saperion.common.lang.collection.CaseInsensitiveMap;
import com.saperion.common.lang.string.Strings;
import com.saperion.common.logging.Logger;

/**
 * Singleton configuration of the Render Service. Reads configuration values from JNDI and 
 * files.
 */
public final class RenderServiceConfiguration {

	private static final Logger LOGGER = Logger.getLogger(RenderServiceConfiguration.class);
	private static final String LOOKUP_PATH = "java:comp/env/";
	private static final String PROPERTIES_FILE_PATH = "/config/";

	private static RenderServiceConfiguration INSTANCE;
	private static boolean initialized;

	private Map<String, String> properties = new CaseInsensitiveMap<>();

	private final String applicationPath;

	private RenderServiceConfiguration(String applicationPath) {
		this.applicationPath = applicationPath;
		loadConfig();
	}

	public static synchronized void initialize(String applicationPath) {
		INSTANCE = new RenderServiceConfiguration(applicationPath);
		initialized = true;
	}

	/**
	 * @return
	 * 		singleton instance
	 */
	public static RenderServiceConfiguration getInstance() {
		if (!initialized) {
			throw new IllegalStateException("Configuration is not initialized.");
		}
		return INSTANCE;
	}

	@TraceLog
	private void loadConfig() {
		FilePropertiesLoader fileLoader = new FilePropertiesLoader(PROPERTIES_FILE_PATH);
		JndiPropertiesLoader jndiLoader = new JndiPropertiesLoader(LOOKUP_PATH);

		ChainedPropertiesLoader chain = new ChainedPropertiesLoader();
		chain.addMandatoryPropertiesLoader(fileLoader);
		chain.addOptionalPropertiesLoader(jndiLoader);

		try {
			Properties props = chain.load(new RenderServicePropertiesResource());
			Enumeration<?> propertyNames = props.propertyNames();

			while (propertyNames.hasMoreElements()) {
				String key = (String) propertyNames.nextElement();
				String property = props.getProperty(key);

				property = property.replace("${webapp-path}", applicationPath);

				properties.put(key, property);
			}
		} catch (ConfigurationException e) {
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
		}
	}

	/**
	 * @param name
	 * 		name of the property to get
	 * @param defaultValue
	 * 		default value that is returned in case the property was not found
	 * @return string-value of the property or default value if not found
	 */
	public String getString(String name, String defaultValue) {
		if (properties.containsKey(name)) {
			return properties.get(name);
		}

		return defaultValue;
	}

	/**
	 * @param name
	 * 		name of the property to get
	 * @param defaultValue
	 * 		default value that is returned in case the property was not found
	 * @return boolean-value of the property or default value if not found
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		if (properties.containsKey(name)) {
			return Boolean.parseBoolean(properties.get(name));
		}

		return defaultValue;
	}

	/**
	 * @param name
	 * 		name of the property to get
	 * @param defaultValue
	 * 		default value that is returned in case the property was not found
	 * @return int-value of the property or default value if not found
	 */
	public int getInt(String name, int defaultValue) {
		if (properties.containsKey(name)) {
			try {
				return Integer.parseInt(properties.get(name));
			} catch (NumberFormatException e) {
				LOGGER.warn("Failed to parse value for property " + name, e);
				return defaultValue;
			}
		}

		return defaultValue;
	}

	/**
	 * Returns a property as a map with key-value pairs. The expected format in the configuration
	 * file is 'name1=value1;name2=value2,...'.
	 * 
	 * @param name
	 * 		name of the property to get
	 * @param defaultValue
	 * 		default value that is returned in case the property was not found
	 * @return value of the property or default value if not found
	 */
	public Map<String, String> getMap(String name, Map<String, String> defaultValue) {
		String string = getString(name, "");

		if (Strings.isBlank(string)) {
			return defaultValue;
		}

		Map<String, String> result = new HashMap<>();
		String[] pairs = string.split(";");

		for (String pair : pairs) {
			String[] nameValue = pair.trim().split("=");

			if (nameValue.length != 2) {
				LOGGER.warn(
						"Failed to convert property to map. Expected format: 'name1=value1;name2=value2,...",
						"name", name, "string", string);
				return defaultValue;
			}

			String vName = nameValue[0].trim();
			String value = nameValue[1].trim();

			result.put(vName, value);
		}

		return result;
	}

	/**
	 * @return unmodifiable map containing all configuration paramters
	 */
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(properties);
	}

	private static class RenderServicePropertiesResource implements PropertiesResource {
		@Override
		public String getName() {
			return "RenderServiceConfiguration";
		}
	}
}
