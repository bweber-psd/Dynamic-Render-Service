package com.perceptivesoftware.renderservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.saperion.common.io.Streams;
import com.saperion.common.logging.Logger;

/**
 * Utility class used to get the current version of the application.
 */
public final class ApplicationVersion {

	private static final Logger LOGGER = Logger.getLogger(ApplicationVersion.class);
	private static String version;

	static {
		InputStream resourceAsStream =
				ApplicationVersion.class.getResourceAsStream("/application-version.txt");

		if (resourceAsStream != null) {
			try {
				version =
						Streams.convertInputStreamToString(resourceAsStream,
								Charset.forName("UTF-8"));
			} catch (IOException e) {
				version = "?";
				LOGGER.error("Failed to read version: " + e.getMessage());
			}
		} else {
			LOGGER.error("application-version.txt was not found.");
			version = "?";
		}
	}

	private ApplicationVersion() {

	}

	/**
	 * @return the version of the application as read from application-version.txt
	 */
	public static String getVersionString() {
		return version;
	}

}
