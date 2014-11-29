package com.perceptivesoftware.renderservice.rs.wrapper;

import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps a {@link TimeoutException} to a {@link Response}.
 */
@Provider
public class TimeoutExceptionMapper implements ExceptionMapper<TimeoutException> {

	@Override
	public Response toResponse(TimeoutException exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity("Timeout reached while waiting for render result.")
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
