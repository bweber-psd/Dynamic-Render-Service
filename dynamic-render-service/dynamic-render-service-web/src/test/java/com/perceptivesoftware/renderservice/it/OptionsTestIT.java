package com.perceptivesoftware.renderservice.it;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.external.ExternalTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Assert;
import org.junit.Test;

import com.perceptivesoftware.renderservice.response.ResponseCreator;

/**
 * Tests if the options for rendering are passed correctly to the render engine.
 */
public class OptionsTestIT extends JerseyTest {

	private static final String FILE_PATH = "./src/test/resources/";
	private static final String RESOURCE_PATH = "renderservice/webapi/rendition";

	@Override
	protected Application configure() {
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages("com.perceptivesoftware.renderservice.rs");
		resourceConfig.register(MultiPartFeature.class);

		return resourceConfig;
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(MultiPartFeature.class);
	}

	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		return new ExternalTestContainerFactory();
	}

	/**
	 * Render PDF without passing any parameters.
	 * 
	 * @param cacheManager
	 * 		mocked cache manager
	 * @throws Exception
	 * 		unexpected exception
	 */
	@Test
	public void testPdfResoureDefaultParams() throws Exception {
		WebTarget target = target(RESOURCE_PATH + "/pdf");

		FormDataMultiPart form = getFormData();

		Response post =
				target.request().post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
		Assert.assertEquals(Status.OK.getStatusCode(), post.getStatus());
		Assert.assertEquals("-1", post.getHeaderString(ResponseCreator.PAGE_HEADER));
		Assert.assertEquals("application/pdf", post.getHeaderString(ResponseCreator.TARGET_HEADER));
		consumeStream(post.getEntity());
	}

	/**
	 * Render PDF and pass a page parameter.
	 * 
	 * @param cacheManager
	 * 		mocked cache manager
	 * @throws Exception
	 * 		unexpected exception
	 */
	@Test
	public void testPdfResoureWithPage() throws Exception {

		WebTarget target = target(RESOURCE_PATH + "/pdf");

		FormDataMultiPart form = getFormDataWithTiff();
		String page = "2";
		form.field("page", page);

		Response post =
				target.request().post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
		Assert.assertEquals(Status.OK.getStatusCode(), post.getStatus());
		Assert.assertEquals(page, post.getHeaderString(ResponseCreator.PAGE_HEADER));
		Assert.assertEquals("application/pdf", post.getHeaderString(ResponseCreator.TARGET_HEADER));
		consumeStream(post.getEntity());
	}

	/**
	 * Render PNG without passing any parameters.
	 * 
	 * @param cacheManager
	 * 		mocked cache manager
	 * @throws Exception
	 * 		unexpected exception
	 */
	@Test
	public void testPngResoureDefaultParams() throws Exception {

		WebTarget target = target(RESOURCE_PATH + "/png");

		FormDataMultiPart form = getFormData();

		Response post =
				target.request().post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
		Assert.assertEquals(Status.OK.getStatusCode(), post.getStatus());
		Assert.assertEquals("1", post.getHeaderString(ResponseCreator.PAGE_HEADER));
		Assert.assertEquals("image/png", post.getHeaderString(ResponseCreator.TARGET_HEADER));
		consumeStream(post.getEntity());
	}

	/**
	 * Render PNG and pass a page parameter.
	 * 
	 * @param cacheManager
	 * 		mocked cache manager
	 * @throws Exception
	 * 		unexpected exception
	 */
	@Test
	public void testPngResoureWithPage() throws Exception {

		WebTarget target = target(RESOURCE_PATH + "/png");

		FormDataMultiPart form = getFormDataWithTiff();
		String page = "2";
		form.field("page", page);

		Response post =
				target.request().post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));
		Assert.assertEquals(Status.OK.getStatusCode(), post.getStatus());
		Assert.assertEquals(post.getMediaType().toString(), "image/png");
		Assert.assertEquals(page, post.getHeaderString(ResponseCreator.PAGE_HEADER));
		Assert.assertEquals("image/png", post.getHeaderString(ResponseCreator.TARGET_HEADER));
		consumeStream(post.getEntity());
	}

	private FormDataMultiPart getFormData() {
		FormDataMultiPart form = new FormDataMultiPart();
		InputStream input = new ByteArrayInputStream("hello".getBytes());
		FormDataContentDisposition disposition =
				FormDataContentDisposition.name("file").fileName("test.txt").build();
		FormDataBodyPart body =
				new FormDataBodyPart(disposition, input, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		form.bodyPart(body);
		return form;
	}

	private FormDataMultiPart getFormDataWithTiff() throws FileNotFoundException {
		FormDataMultiPart form = new FormDataMultiPart();
		InputStream input = new FileInputStream(FILE_PATH + "A4H_Seiten3.TIF");
		FormDataContentDisposition disposition =
				FormDataContentDisposition.name("file").fileName("A4H_Seiten3.TIF").build();
		FormDataBodyPart body =
				new FormDataBodyPart(disposition, input, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		form.bodyPart(body);
		return form;
	}

	private void consumeStream(Object responseEntity) throws IOException {
		if (responseEntity instanceof InputStream) {
			try (InputStream stream = (InputStream) responseEntity) {

				byte[] buf = new byte[8192];

				while (stream.read(buf) > -1) {
					continue;
				}

			}
		}
	}
}
