package com.perceptivesoftware.renderservice.options;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.saperion.connector.options.AdditionalOptions;
import com.saperion.connector.options.Options;
import com.saperion.connector.options.Options.BuilderChain;
import com.saperion.connector.options.paging.Paging;
import com.saperion.connector.render.engine.RenderEngine;

/**
 * Helper class to create {@link Options}.
 */
public final class OptionsCreator {

	private static final Map<String, String> ENGINE_OPTIONS_FROM_CONFIG;

	static {
		ENGINE_OPTIONS_FROM_CONFIG =
				RenderServiceConfiguration.getInstance().getMap(
						ConfigurationKeys.RENDER_ENGINEOPTIONS, new HashMap<String, String>());
	}

	private final String renderEngineClass;

	private OptionsCreator(String renderEngineClass) {
		this.renderEngineClass = renderEngineClass;
	}

	/**
	 * @param engine
	 * 		render engine to be used
	 * @return new instance of {@link OptionsCreator}
	 */
	@TraceLog
	public static OptionsCreator forEngine(RenderEngine engine) {
		return new OptionsCreator(engine.getClass().getCanonicalName());
	}

	/**
	 * @param renderOptions
	 * 		{@link RenderOptions} to use
	 * @return {@link Options} with options for specified engine from configuration and from
	 * 		specified render options
	 */
	@TraceLog
	public Options withRenderOptions(RenderOptions renderOptions) {
		BuilderChain options = Options.newOptions(renderOptions.getTargetFormat());

		int page = renderOptions.getPage();
		if (page > 0) {
			options.definePaging(Paging.from(page).to(page));
		}

		Map<String, Object> genericOptions = new HashMap<>();
		genericOptions.putAll(ENGINE_OPTIONS_FROM_CONFIG);

		AdditionalOptions additionalOptions =
				AdditionalOptions.newAdditionalOptions().genericOptions(genericOptions)
						.dpi(renderOptions.getDpi())
						.imageWidthAndHeight(renderOptions.getWidth(), renderOptions.getHeight())
						.build();

		return options.additionalOptions(additionalOptions)
				.timeOut(renderOptions.getTimeout(), TimeUnit.SECONDS).build();
	}
}
