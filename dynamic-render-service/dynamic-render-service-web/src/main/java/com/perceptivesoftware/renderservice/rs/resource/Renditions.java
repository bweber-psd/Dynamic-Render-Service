package com.perceptivesoftware.renderservice.rs.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.osgi.framework.BundleContext;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.perceptivesoftware.renderservice.exception.ContentTooLargeException;
import com.perceptivesoftware.renderservice.exception.NoRenderEngineException;
import com.perceptivesoftware.renderservice.options.RenderOptions;
import com.perceptivesoftware.renderservice.osgi.OSGiUtil;
import com.perceptivesoftware.renderservice.render.RenditionCreator;
import com.perceptivesoftware.renderservice.response.ResponseCreator;
import com.perceptivesoftware.renderservice.util.InputStreamDescriptorCreator;
import com.perceptivesoftware.renderservice.util.TargetFormatCreator;
import com.saperion.common.io.InputStreamDescriptor;
import com.saperion.common.lang.string.Strings;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.renditions.Rendition;
import com.saperion.connector.renditions.exceptions.RenderingException;

/**
 * Resource for content-based rendering.
 */
@Path("rendition")
public class Renditions extends AbstractResource {
	 @Context
	  private ServletContext servletContext;
	 
	@PostConstruct
	public void postConstruct() {
		BundleContext bundleContext = OSGiUtil.getBundleContext(servletContext);
	}
	
	/**
	 * Renders the provided content to the specified format. Content to render and optional 
	 * parameters are expected to be sent as multipart/formdata.
	 * 
	 * @param timeoutParam
	 * 		maximum timeout to wait for rendition (optional, if not set or < 1, configured
	 * 		default value will be used)
	 * @param dpiParam
	 * 		DPI for rendition (optional, if not set or < 1, configured
	 * 		default value will be used)
	 * @param pageParam
	 * 		page to render, or -1 for all pages (optional, default is -1)
	 * @param heightParam
	 * 		height of the rendition or -1 for auto-height (optional, default is -1)
	 * @param widthParam
	 * 		width of the rendition or -1 for auto-width (optional, default is -1)
	 * @param file
	 * 		content to render
	 * @param fileDisposition
	 * 		meta-data of the content to render
	 * @param extension
	 * 		target format extension
	 * @return {@link Response} with result of rendering or error message
	 * @throws TimeoutException
	 * 		timeout reached while waiting for the rendition
	 * @throws RenderingException
	 * 		exception while rendering
	 * @throws InterruptedException
	 * 		rendering thread was interrupted
	 * @throws ExecutionException
	 * 		exception while rendering
	 * @throws IOException 
	 * 		I/O error when reading content
	 * @throws ContentTooLargeException
	 * 		when the received content is larger than allowed
	 * @throws NoRenderEngineException 
	 * 		when no render engine is available
	 */
	@POST
	@Path("/{extension}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@TraceLog
	public Response render(@FormDataParam(TIMEOUT_PARAM) @DefaultValue("0") int timeoutParam,
			@FormDataParam(DPI_PARAM) @DefaultValue("0") int dpiParam,
			@FormDataParam(PAGE_PARAM) @DefaultValue("-1") int pageParam,
			@FormDataParam(HEIGHT_PARAM) @DefaultValue("-1") int heightParam,
			@FormDataParam(WIDTH_PARAM) @DefaultValue("-1") int widthParam,
			@FormDataParam(FILE_PARAM) InputStream file,
			@FormDataParam(FILE_PARAM) FormDataContentDisposition fileDisposition,
			@PathParam("extension") String extension) throws TimeoutException, RenderingException,
			InterruptedException, ExecutionException, IOException, ContentTooLargeException,
			NoRenderEngineException {

		if (file == null || fileDisposition == null
				|| Strings.isNullOrEmpty(fileDisposition.getFileName())) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Form-data parameter '" + FILE_PARAM + "' not present.")
					.type(MediaType.TEXT_PLAIN_TYPE).build();
		}

		int dpi = getDpi(dpiParam);
		int timeout = getTimeout(timeoutParam);
		TargetFormat targetFormat = TargetFormatCreator.fromString(extension);
		int page = getPage(pageParam, targetFormat);

		InputStreamDescriptor content =
				InputStreamDescriptorCreator.fromParameters(file, fileDisposition);

		RenderOptions options =
				new RenderOptions(targetFormat, page, dpi, timeout, heightParam, widthParam);

		Rendition rendition = new RenditionCreator(content, options).getSingleRendition();
		return ResponseCreator.forRendition(rendition);
	}

}
