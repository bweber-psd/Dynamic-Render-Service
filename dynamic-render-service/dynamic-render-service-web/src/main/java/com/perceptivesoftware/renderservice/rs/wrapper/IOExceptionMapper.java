package com.perceptivesoftware.renderservice.rs.wrapper;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps an {@link IOException} to a {@link Response}.
 */
@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

	@Override
	public Response toResponse(IOException exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
