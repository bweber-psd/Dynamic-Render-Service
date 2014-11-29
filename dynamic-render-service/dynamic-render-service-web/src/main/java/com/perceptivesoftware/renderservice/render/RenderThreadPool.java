package com.perceptivesoftware.renderservice.render;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.saperion.common.logging.Logger;
import com.saperion.connector.renditions.Rendition;

/**
 * Singleton thread-pool for rendering. Gets initialied and shut down by the servlet-context-
 * listener.
 */
public final class RenderThreadPool {

	private static final Logger LOGGER = Logger.getLogger(RenderThreadPool.class);
	private static ExecutorService executor;

	private static boolean initialized;
	private static boolean shutDown;

	private RenderThreadPool() {

	}

	/**
	 * Initializes the thread-pool.
	 */
	public static void initialize() {

		if (initialized) {
			throw new IllegalStateException("Render thread pool was already initialized.");
		}

		int availableCores = Runtime.getRuntime().availableProcessors() / 2;
		int maxThreads =
				RenderServiceConfiguration.getInstance().getInt(ConfigurationKeys.RENDER_THREADS,
						availableCores);
		maxThreads = Math.max(maxThreads, 1);

		executor = Executors.newFixedThreadPool(maxThreads);

		LOGGER.info("Render thread pool initialized with " + maxThreads + " threads");
		initialized = true;
	}

	/**
	 * Submits a new job to the pool.
	 * 
	 * @param job
	 * 		job to submit
	 * @return a {@link Future} with the result of the job
	 */
	public static Future<List<Rendition>> submitJob(RenderJob job) {
		if (!initialized) {
			throw new IllegalStateException("Render thread pool was not yet initialized.");
		}
		if (shutDown) {
			throw new IllegalStateException("Render thread pool was already shut down.");
		}

		return executor.submit(job);
	}

	/**
	 * Shuts down the thread-pool and releases all resources.
	 */
	public static void shutdown() {
		executor.shutdown();
		LOGGER.info("Render thread pool was shut down.");
		initialized = false;
		shutDown = true;
	}
}
