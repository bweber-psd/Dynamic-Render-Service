package com.perceptivesoftware.renderservice.util;

import java.util.Set;

import javax.ws.rs.BadRequestException;

import com.perceptivesoftware.renderservice.exception.NoRenderEngineException;
import com.perceptivesoftware.renderservice.render.EngineLoader;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.render.engine.RenderEngine;

/**
 * Utility class used to get a {@link TargetFormat}.
 */
public final class TargetFormatCreator {

	private TargetFormatCreator() {

	}

	/**
	 * Returns the {@link TargetFormat} that matches the provided extension string.
	 * If no matching and supported format is found, a {@link BadRequestException} 
	 * is thrown.
	 * 
	 * @param format
	 * 		format extension
	 * @return matching {@link TargetFormat}
	 * @throws NoRenderEngineException
	 * 		when no render engine is available
	 */
	public static TargetFormat fromString(String format) throws NoRenderEngineException {
		RenderEngine engine = EngineLoader.load();

		if (engine == null) {
			throw new NoRenderEngineException();
		}

		Set<TargetFormat> supportedTargetFormats = engine.getSupportedTargetFormats();

		for (TargetFormat targetFormat : supportedTargetFormats) {
			if (targetFormat.getExtension().equalsIgnoreCase(format)) {
				return targetFormat;
			}
		}

		throw new BadRequestException("Target format " + format + " not supported.");
	}

}
