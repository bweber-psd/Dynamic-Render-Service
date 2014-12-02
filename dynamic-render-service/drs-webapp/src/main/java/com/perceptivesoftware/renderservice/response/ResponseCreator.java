package com.perceptivesoftware.renderservice.response;

import javax.ws.rs.core.Response;

import com.saperion.connector.renditions.Rendition;

/**
 * Creates a {@link javax.ws.rs.core.Response} for renditions. The response will contain the
 * following headers:
 * 
 * <ul>
 * <li>X-Renderservice-Target (target format of the rendition)
 * <li>X-Renderservice-Page (rendered page or -1 for all pages)
 * </ul>
 */
public final class ResponseCreator {

	/** Name of the header containing the target format. */
	public static final String TARGET_HEADER = "X-Renderservice-Target";
	/** Name of the header containing the page number. */
	public static final String PAGE_HEADER = "X-Renderservice-Page";

	private ResponseCreator() {

	}

	/**
	 * Creates a response for the specified rendition.
	 * 
	 * @param rendition
	 * 		the rendition that will be contained in the response
	 * @return response with rendition to stream and headers
	 * @throws Exception 
	 */
	public static Response forRendition(Rendition rendition, String fileName) throws Exception {
		String mimetype = rendition.getTarget().getMimetype();
		mimetype = "application/octet-stream";
		return Response.ok().header(PAGE_HEADER, rendition.getPage())
				.header(TARGET_HEADER, mimetype).header("content-disposition", "attachment; filename=\"" + fileName +"\"").type(mimetype)
				.entity(new RenditionStreamingOutput(rendition)).build();
	}
}
