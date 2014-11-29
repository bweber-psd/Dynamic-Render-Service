package com.perceptivesoftware.renderservice.exception;

/**
 * Exception thrown when no render-engine was found in the classpath.
 */
public class NoRenderEngineException extends Exception {

	private static final long serialVersionUID = 148868629329089981L;

	/**
	 * Default constructor.
	 */
	public NoRenderEngineException() {
		super("No render engine implementation was found in classpath.");
	}
}
