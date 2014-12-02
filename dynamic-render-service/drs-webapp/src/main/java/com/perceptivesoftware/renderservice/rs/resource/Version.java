package com.perceptivesoftware.renderservice.rs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.perceptivesoftware.renderservice.util.ApplicationVersion;

/**
 * API version resource.
 */
@Path("version")
public class Version extends AbstractResource {

	/**
	 * @return Version as a text/plain response.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@TraceLog
	public String getVersion() {
		return ApplicationVersion.getVersionString();
	}
}
