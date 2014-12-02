package com.perceptivesoftware.renderservice.rs.wrapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.perceptivesoftware.renderservice.exception.NoRenderEngineException;

/**
 * Maps a {@link NoRenderEngineException} to a {@link Response}.
 */
@Provider
public class NoRenderEngineExceptionMapper implements ExceptionMapper<NoRenderEngineException> {

	@Override
	public Response toResponse(NoRenderEngineException exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
