package com.perceptivesoftware.renderservice.rs.wrapper;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps an {@link ExecutionException} to a {@link Response}.
 */
@Provider
public class ExecutionExceptionMapper implements ExceptionMapper<ExecutionException> {

	@Override
	public Response toResponse(ExecutionException exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
