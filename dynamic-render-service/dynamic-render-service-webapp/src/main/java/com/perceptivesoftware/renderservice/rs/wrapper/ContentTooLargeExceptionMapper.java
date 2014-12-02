package com.perceptivesoftware.renderservice.rs.wrapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.perceptivesoftware.renderservice.exception.ContentTooLargeException;

/**
 * Maps a {@link ContentTooLargeException} to a {@link Response}.
 */
@Provider
public class ContentTooLargeExceptionMapper implements ExceptionMapper<ContentTooLargeException> {

	@Override
	public Response toResponse(ContentTooLargeException exception) {
		return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

}
