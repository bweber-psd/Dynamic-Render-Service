package com.perceptivesoftware.renderservice;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * The {@link javax.ws.rs.core.Application} for the render service.
 */
@ApplicationPath("/webapi")
public class RenderServiceApplication extends ResourceConfig {

	/**
	 * Registers REST resources and additional features.
	 */
	public RenderServiceApplication() {
		packages("com.perceptivesoftware.renderservice.rs");
		register(MultiPartFeature.class);
	}
}
