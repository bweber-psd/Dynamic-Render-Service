package com.perceptivesoftware.renderservice.cache;

import com.saperion.cache.Cache;
import com.saperion.cache.CacheFactory;
import com.saperion.cache.EmptyFileCacheImpl;
import com.saperion.cache.FileCache;
import com.saperion.cache.FileCacheImpl;
import com.saperion.cache.config.CacheBuilder;
import com.saperion.cache.config.CacheConfiguration;
import com.saperion.cache.config.xml.CacheType;
import com.saperion.cache.data.FileCacheEntry;
import com.saperion.cache.exception.BuilderException;
import com.saperion.cache.policy.PolicyMap;
import com.saperion.common.logging.Logger;

/**
 * A {@link CacheFactory} that creates a {@link FileCache} which uses a 
 * {@link RenderServiceCacheKey}.
 */
public class RenderServiceCacheFactory extends
		CacheFactory<RenderServiceCacheKey, FileCacheEntry<RenderServiceCacheKey>> {

	private static final Logger LOGGER = Logger.getLogger(RenderServiceCacheFactory.class);

	@SuppressWarnings("unchecked")
	@Override
	public Cache<RenderServiceCacheKey, FileCacheEntry<RenderServiceCacheKey>> createCache(
			Cache<RenderServiceCacheKey, FileCacheEntry<RenderServiceCacheKey>> cache,
			CacheType cacheConfig) {
		PolicyMap<RenderServiceCacheKey, FileCacheEntry<RenderServiceCacheKey>> policyMap =
				getPolicyMap(cacheConfig.getEvictionPolicy());
		FileCache<RenderServiceCacheKey> fileCache = null;
		try {
			@SuppressWarnings("rawtypes")
			Class cacheClass = FileCacheImpl.class;
			if (!cacheConfig.isStart()) {
				cacheClass = EmptyFileCacheImpl.class;
			}
			fileCache =

					new CacheBuilder<FileCache<RenderServiceCacheKey>>().type(cacheClass)
							.cacheManager(cache.getCacheManager())
							.configuration(new CacheConfiguration(cacheConfig)).map(policyMap)
							.build();

			CacheFactory.buildAndSetHashHandler(fileCache, cacheConfig);

		} catch (BuilderException e) {
			LOGGER.error("Failed to build cache." + e);
		}
		return fileCache;
	}

}
