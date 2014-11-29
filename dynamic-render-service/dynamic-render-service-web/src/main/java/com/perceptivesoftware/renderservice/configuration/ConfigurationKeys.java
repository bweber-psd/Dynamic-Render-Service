package com.perceptivesoftware.renderservice.configuration;

/**
 * Keys of configuration properties.
 */
public final class ConfigurationKeys {

	/** Resolution of renditions (DPI). */
	public static final String RENDER_DPI = "render.dpi";

	/** Default timeout for render-process. */
	public static final String RENDER_TIMEOUT = "render.timeout";

	/** Maximum number of threads used for rendering. */
	public static final String RENDER_THREADS = "render.threads";

	/** Options for the render-engine. */
	public static final String RENDER_ENGINEOPTIONS = "render.engineoptions";

	/** Size of buffer to use for streaming. */
	public static final String MEMORY_BUFFERSIZE = "memory.buffersize";

	/** Size of temporary buffer in memory. */
	public static final String MEMORY_TEMPSIZE = "memory.tempsize";

	/** Maximum size of received content (MB). */
	public static final String MEMORY_MAXRECEIVESIZE = "memory.maxreceivesize";

	private ConfigurationKeys() {

	}
}
