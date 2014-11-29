package com.perceptivesoftware.renderservice.render;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.perceptivesoftware.renderservice.exception.NoRenderEngineException;
import com.perceptivesoftware.renderservice.options.OptionsCreator;
import com.perceptivesoftware.renderservice.options.RenderOptions;
import com.saperion.common.io.InputStreamDescriptor;
import com.saperion.common.logging.Logger;
import com.saperion.connector.options.Options;
import com.saperion.connector.render.engine.RenderEngine;
import com.saperion.connector.renditions.Rendition;
import com.saperion.connector.renditions.exceptions.RenderingException;

/**
 * A {@link Callable} that performs the actual rendering on the engine when called.
 */
public class RenderJob implements Callable<List<Rendition>> {

	private static final Logger LOGGER = Logger.getLogger(RenderJob.class);

	private final InputStreamDescriptor input;
	private final RenderOptions options;

	private RenderEngine engine;

	/**
	 * @param input
	 * 		content to render
	 * @param options
	 * 		rendering options
	 */
	public RenderJob(InputStreamDescriptor input, RenderOptions options) {
		this.input = input;
		this.options = options;
		this.engine = EngineLoader.load();
	}

	@Override
	@TraceLog
	public List<Rendition> call() throws TimeoutException, RenderingException,
			NoRenderEngineException {
		if (engine != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Starting to render.", "engine", engine.getClass().getCanonicalName(),
						"options", options);
			}
			Options engineOptions = OptionsCreator.forEngine(engine).withRenderOptions(options);
			return engine.render(input, engineOptions);
		} else {
			throw new NoRenderEngineException();
		}
	}

}
