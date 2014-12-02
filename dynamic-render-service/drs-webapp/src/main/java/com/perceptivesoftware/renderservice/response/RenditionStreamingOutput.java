package com.perceptivesoftware.renderservice.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.perceptivesoftware.renderservice.configuration.ConfigurationDefaults;
import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.saperion.common.io.Streams;
import com.saperion.common.logging.Logger;
import com.saperion.connector.renditions.Rendition;

/**
 * Represents a response with a single rendition. The content will be streamed to the client.
 */
public class RenditionStreamingOutput implements StreamingOutput {

	private static final Logger LOGGER = Logger.getLogger(StreamingOutput.class);

	private final int bufferSize = RenderServiceConfiguration.getInstance().getInt(
			ConfigurationKeys.MEMORY_BUFFERSIZE, ConfigurationDefaults.MEMORY_BUFFERSIZE);

	private final Rendition rendition;

	/**
	 * @param rendition
	 * 		rendition that will be streamed to the client
	 */
	public RenditionStreamingOutput(Rendition rendition) {
		this.rendition = rendition;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		try (InputStream input = rendition.getStream().getInputStream()) {
			Streams.stream(input, output, bufferSize);
		} catch (IOException e) {
			throw e;
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
			throw new WebApplicationException(e, Response.serverError().entity(e.getMessage())
					.type(MediaType.TEXT_PLAIN_TYPE).build());
		}
	}
}
