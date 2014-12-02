package com.perceptivesoftware.renderservice.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.saperion.common.io.InputStreamDescriptor;

/**
 * Helper class used to create an {@link InputStreamDescriptor}.
 */
public final class InputStreamDescriptorCreator {

	private InputStreamDescriptorCreator() {
	}

	/**
	 * Creates an {@link InputStreamDescriptor} based on the specified parameters.
	 * 
	 * @param stream
	 * 		stream to describe
	 * @param contentDisposition
	 * 		meta-data for the stream
	 * @return {@link InputStreamDescriptor} with stream and meta-data
	 * @throws MalformedURLException
	 * 		when the filename was not acceptable
	 */
	public static InputStreamDescriptor fromParameters(InputStream stream,
			FormDataContentDisposition contentDisposition) throws MalformedURLException {
		return new InputStreamDescriptor(stream, new URL("file", null,
				contentDisposition.getFileName()));
	}
	
	public static InputStreamDescriptor fromParameters(InputStream stream, String fileName) throws MalformedURLException {
		return new InputStreamDescriptor(stream, new URL("file", null, fileName));
	}
	
}
