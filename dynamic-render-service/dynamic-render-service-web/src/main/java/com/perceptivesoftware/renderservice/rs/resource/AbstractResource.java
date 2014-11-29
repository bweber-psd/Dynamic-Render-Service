package com.perceptivesoftware.renderservice.rs.resource;

import com.perceptivesoftware.renderservice.configuration.ConfigurationDefaults;
import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.formats.TargetFormatType;

/**
 * Abstract superclass for all resources. Contains common methods and constants.
 */
public abstract class AbstractResource {

	protected static final String WIDTH_PARAM = "width";
	protected static final String HEIGHT_PARAM = "height";
	protected static final String FILE_PARAM = "file";
	protected static final String PAGE_PARAM = "page";
	protected static final String DPI_PARAM = "dpi";
	protected static final String TIMEOUT_PARAM = "timeout";

	private final RenderServiceConfiguration configuration = RenderServiceConfiguration
			.getInstance();

	/**
	 * @param timeoutParam
	 * 		timeout parameter from request
	 * @return value from request-parameter or from config, if not set in request
	 */
	protected int getTimeout(int timeoutParam) {
		if (timeoutParam > 0) {
			return timeoutParam;
		}

		return configuration.getInt(ConfigurationKeys.RENDER_TIMEOUT,
				ConfigurationDefaults.RENDER_TIMEOUT);
	}

	/**
	 * @param dpiParam
	 * 		DPI parameter from request
	 * @return value from request-parameter or from config, if not set in request
	 */
	protected int getDpi(int dpiParam) {
		if (dpiParam > 0) {
			return dpiParam;
		}

		return configuration.getInt(ConfigurationKeys.RENDER_DPI, ConfigurationDefaults.RENDER_DPI);
	}

	protected int getPage(int pageParam, TargetFormat target) {
		if (target.getType() == TargetFormatType.SINGLE_PAGE && pageParam == -1) {
			return 1;
		}

		return pageParam;
	}
}
