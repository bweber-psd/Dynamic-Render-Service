package com.perceptivesoftware.renderservice.rs.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.perceptivesoftware.renderservice.render.EngineLoader;
import com.saperion.connector.formats.SourceFormat;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.formats.serializable.SerializableSourceFormat;
import com.saperion.connector.formats.serializable.SerializableTargetFormat;
import com.saperion.connector.render.engine.RenderEngine;

/**
 * Resource for source- and target-formats of the underlying render-engine.
 */
@Path("formats")
public class Formats {

	/**
	 * @return the supported source-formats of the underliying render-engine
	 */
	@GET
	@Path("sources")
	@Produces(MediaType.APPLICATION_JSON)
	@TraceLog
	public SerializableSourceFormat[] getSourceFormats() {
		RenderEngine engine = EngineLoader.load();

		Set<SourceFormat> supportedSourceFormats = engine.getSupportedSourceFormats();
		Set<SerializableSourceFormat> result = new HashSet<SerializableSourceFormat>();

		for (SourceFormat sourceFormat : supportedSourceFormats) {
			result.add(SerializableSourceFormat.fromSourceFormat(sourceFormat));
		}

		return result.toArray(new SerializableSourceFormat[result.size()]);
	}

	/**
	 * @return the supported target-formats of the underlying render-engine.
	 */
	@GET
	@Path("targets")
	@Produces(MediaType.APPLICATION_JSON)
	@TraceLog
	public SerializableTargetFormat[] getTargetFormats() {
		RenderEngine engine = EngineLoader.load();

		Set<TargetFormat> supportedTargetFormats = engine.getSupportedTargetFormats();
		Set<SerializableTargetFormat> result = new HashSet<SerializableTargetFormat>();

		for (TargetFormat targetFormat : supportedTargetFormats) {
			result.add(SerializableTargetFormat.fromTargetFormat(targetFormat));
		}

		return result.toArray(new SerializableTargetFormat[result.size()]);
	}
}
