package com.perceptivesoftware.renderservice.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Exception thrown when more than one rendition is returned by the engine.
 */
public class TooManyRenditionsException extends WebApplicationException {

	private static final long serialVersionUID = -4587538589582893389L;

	public TooManyRenditionsException() {
		super("Engine returned more than one rendition for "
				+ "the requested document and target format.", Status.BAD_REQUEST);
	}

}
