package com.perceptivesoftware.renderservice.rs.wrapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.saperion.connector.renditions.exceptions.RenderingException;

/**
 * Maps an {@link RenderingException} to a {@link Response}.
 */
@Provider
public class RenderingExceptionMapper implements ExceptionMapper<RenderingException> {

	@Override
	public Response toResponse(RenderingException exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
