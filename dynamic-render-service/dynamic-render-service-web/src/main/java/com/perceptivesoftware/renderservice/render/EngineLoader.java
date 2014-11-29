package com.perceptivesoftware.renderservice.render;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.saperion.common.logging.Logger;
import com.saperion.connector.render.engine.RenderEngine;

/**
 * Loads the render-engine.
 */
public final class EngineLoader {

	private static final Logger LOGGER = Logger.getLogger(EngineLoader.class);

	private EngineLoader() {

	}

	/**
	 * @return the render engine loaded from the classpath
	 */
	public static RenderEngine load() {
		ServiceLoader<RenderEngine> loader =
				ServiceLoader.load(com.saperion.connector.render.engine.RenderEngine.class);
		Iterator<RenderEngine> enginesIterator = loader.iterator();

		if (enginesIterator.hasNext()) {
			RenderEngine engine = enginesIterator.next();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Using engine " + engine.getClass().getCanonicalName());
			}

			return engine;
		}

		return null;
	}
}
