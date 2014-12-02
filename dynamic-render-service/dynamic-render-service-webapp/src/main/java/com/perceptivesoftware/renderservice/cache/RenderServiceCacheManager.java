package com.perceptivesoftware.renderservice.cache;

import com.saperion.cache.CacheManager;
import com.saperion.cache.FileCache;
import com.saperion.cache.data.FileCacheEntry;
import com.saperion.cache.exception.CacheException;
import com.saperion.common.logging.Logger;

/**
 * Singleton manager for the render service cache.
 */
public final class RenderServiceCacheManager {

	private static final String RENDITION_CACHE = "RenderServiceCache";
	private static final Logger LOGGER = Logger.getLogger(RenderServiceCacheManager.class);
	private static final RenderServiceCacheManager INSTANCE = new RenderServiceCacheManager();

	private boolean initialized;
	private boolean shutdown;
	private CacheManager manager;

	private RenderServiceCacheManager() {

	}

	/**
	 * @return singleton instance
	 */
	public static RenderServiceCacheManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Initializes the cache manager. Must be called before the first call to getInstance.
	 * 
	 * @param applicationIdentifier
	 * 		identifier of the application used to distinguish cache-management-mbeans
	 */
	public synchronized void initialize(String applicationIdentifier) {
		if (initialized) {
			return;
		}

		try {
			this.manager =
					new CacheManager(new RenderServiceCacheConfigLoader(applicationIdentifier));
			initialized = true;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Cache manager initialized.", "applicationIdentifier",
						applicationIdentifier);
			}
		} catch (CacheException e) {
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
		}
	}

	/**
	 * Shuts down the cache manager.
	 */
	public synchronized void shutdown() {
		if (!initialized) {
			throw new IllegalStateException("Cache manager is not initialized.");
		}

		manager.shutdown();
		shutdown = true;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Cache manager shut down.");
		}
	}

	/**
	 * @return the cache that contains the renditions
	 * @throws CacheException
	 * 		when the render cache is not available
	 */
	public synchronized FileCache<RenderServiceCacheKey> getRenderCache() throws CacheException {
		if (shutdown) {
			throw new IllegalStateException("Cache manager is shut down.");
		}
		if (!initialized) {
			throw new IllegalStateException("Cache manager is not initialized.");
		}

		return (FileCache<RenderServiceCacheKey>) manager
				.<RenderServiceCacheKey, FileCacheEntry<RenderServiceCacheKey>> getCache(RENDITION_CACHE);
	}
}
