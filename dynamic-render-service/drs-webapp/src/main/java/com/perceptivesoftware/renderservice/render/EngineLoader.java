package com.perceptivesoftware.renderservice.render;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.osgi.framework.BundleContext;

import com.psd.drs.service.BundleManager;
import com.psd.rendering.drs.osgi.api.RenderService;
import com.saperion.common.logging.Logger;
import com.saperion.connector.render.engine.RenderEngine;

/**
 * Loads the render-engine.
 */
public final class EngineLoader {

	private static final Logger LOGGER = Logger.getLogger(EngineLoader.class);

	
	private EngineLoader() {

	}

//	public static RenderEngine load() {
//		return null;
//	}
	
	/**
	 * @return the render engine loaded from the classpath
	 */
	public static RenderEngine load(BundleManager bundleManager) {
//		ServiceLoader<RenderEngine> loader =
//				ServiceLoader.load(com.saperion.connector.render.engine.RenderEngine.class);
//		Iterator<RenderEngine> enginesIterator = loader.iterator();

		RenderService renderService = bundleManager.getRenderService();
		renderService.getRenderEngine();
		
//		if (enginesIterator.hasNext()) {
//			RenderEngine engine = enginesIterator.next();
//
//			if (LOGGER.isDebugEnabled()) {
//				LOGGER.debug("Using engine " + engine.getClass().getCanonicalName());
//			}
//
//			return engine;
//		}

		return null;
	}
}
