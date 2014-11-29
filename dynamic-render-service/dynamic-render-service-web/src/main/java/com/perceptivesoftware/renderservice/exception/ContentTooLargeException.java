package com.perceptivesoftware.renderservice.exception;

/**
 * Exception thrown when the conent sent from the client is larger than the allowed maximum.
 */
public class ContentTooLargeException extends Exception {

	private static final long serialVersionUID = 6898632161199221059L;

	/**
	 * @param message
	 * 		error message
	 */
	public ContentTooLargeException(String message) {
		super(message);
	}
}
