package com.psd.drs.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.perceptivesoftware.renderservice.configuration.ConfigurationDefaults;
import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.perceptivesoftware.renderservice.options.RenderOptions;
import com.perceptivesoftware.renderservice.render.RenditionCreator;
import com.perceptivesoftware.renderservice.response.ResponseCreator;
import com.perceptivesoftware.renderservice.rs.resource.AbstractResource;
import com.perceptivesoftware.renderservice.util.InputStreamDescriptorCreator;
import com.perceptivesoftware.renderservice.util.TargetFormatCreator;
import com.psd.drs.service.BundleManager;
import com.saperion.common.io.InputStreamDescriptor;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.formats.TargetFormatType;
import com.saperion.connector.renditions.Rendition;

/**
 * Resource for content-based rendering.
 */
@Path("/rendition")
@RequestScoped
public class RenditionRESTService extends AbstractResource {

	@Inject
	BundleManager bundleManager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> listAllBundles() {
		return bundleManager.getAllBundles();
	}

	/**
	 * Renders the provided content to the specified format. Content to render
	 * and optional parameters are expected to be sent as multipart/formdata.
	 * 
	 * @param timeoutParam
	 *            maximum timeout to wait for rendition (optional, if not set or
	 *            < 1, configured default value will be used)
	 * @param dpiParam
	 *            DPI for rendition (optional, if not set or < 1, configured
	 *            default value will be used)
	 * @param pageParam
	 *            page to render, or -1 for all pages (optional, default is -1)
	 * @param heightParam
	 *            height of the rendition or -1 for auto-height (optional,
	 *            default is -1)
	 * @param widthParam
	 *            width of the rendition or -1 for auto-width (optional, default
	 *            is -1)
	 * @param file
	 *            content to render
	 * @param fileDisposition
	 *            meta-data of the content to render
	 * @param extension
	 *            target format extension
	 * @return {@link Response} with result of rendering or error message
	 * @throws Exception
	 */
	@POST
	@Path("/{extension}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response render(MultipartFormDataInput input, @PathParam("extension") String extension)
			throws Exception {
		Map<String, List<InputPart>> formParts = input.getFormDataMap();

		//
		// if (file == null || fileDisposition == null
		// || Strings.isNullOrEmpty(fileDisposition.getFileName())) {
		// return Response.status(Status.BAD_REQUEST)
		// .entity("Form-data parameter '" + FILE_PARAM + "' not present.")
		// .type(MediaType.TEXT_PLAIN_TYPE).build();
		// }

		List<InputPart> inPart = formParts.get("file");
		String fileName = null;
		InputStream file = null;
		for (InputPart inputPart : inPart) {

			// Retrieve headers, read the Content-Disposition header to obtain
			// the original name of the file
			MultivaluedMap<String, String> headers = inputPart.getHeaders();
			fileName = parseFileName(headers);

			// Handle the body of that part with an InputStream
			file = inputPart.getBody(InputStream.class, null);
		}

		int dpi = getDpi(0);
		int timeout = getTimeout(0);
		TargetFormat targetFormat = TargetFormatCreator.fromString(extension);
		int page = getPage(-1, targetFormat);

		InputStreamDescriptor content = InputStreamDescriptorCreator.fromParameters(file, fileName);

		RenderOptions options = new RenderOptions(targetFormat, page, dpi, timeout, -1, -1);

		fileName = renameFileExtension(fileName, extension);
		Rendition rendition =
				new RenditionCreator(content, options, bundleManager).getSingleRendition();
		return ResponseCreator.forRendition(rendition, fileName);
	}

	// Parse Content-Disposition header to get the original file name
	private String parseFileName(MultivaluedMap<String, String> headers) {
		String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
		for (String name : contentDispositionHeader) {
			if ((name.trim().startsWith("filename"))) {
				String[] tmp = name.split("=");
				String fileName = tmp[1].trim().replaceAll("\"", "");
				return fileName;
			}
		}
		return "randomName";
	}

	public String renameFileExtension(String source, String newExtension) {
		String target;
		String currentExtension = getFileExtension(source);

		if (currentExtension.equals("")) {
			target = source + "." + newExtension;
		} else {
			target =
					source.replaceFirst(Pattern.quote("." + currentExtension) + "$",
							Matcher.quoteReplacement("." + newExtension));

		}
		return target;
	}

	public String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1);
		}
		return ext;
	}

}
