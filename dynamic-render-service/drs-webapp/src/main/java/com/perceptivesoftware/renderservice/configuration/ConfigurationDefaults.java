package com.perceptivesoftware.renderservice.configuration;

/**
 * Default values for configuration properties.
 */
public final class ConfigurationDefaults {

	/** Time to wait for render-process. */
	public static final int RENDER_TIMEOUT = 10;

	/** Default value for DPI for renditions. */
	public static final int RENDER_DPI = 200;

	/** Size of buffer for streaming (bytes). */
	public static final int MEMORY_BUFFERSIZE = 8192;

	/** Number of bytes to keep in memory for temp-files. */
	public static final int MEMORY_TEMPSIZE = 102400;

	/** Maximum size for received content (MB). */
	public static final int MEMORY_MAXRECEIVESIZE = 100;

	private ConfigurationDefaults() {

	}
}
