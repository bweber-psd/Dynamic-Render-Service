package com.perceptivesoftware.renderservice.render;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.codec.binary.Hex;

import com.perceptivesoftware.renderservice.annotation.TraceLog;
import com.perceptivesoftware.renderservice.cache.RenderServiceCacheKey;
import com.perceptivesoftware.renderservice.cache.RenderServiceCacheManager;
import com.perceptivesoftware.renderservice.configuration.ConfigurationDefaults;
import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.perceptivesoftware.renderservice.exception.ContentTooLargeException;
import com.perceptivesoftware.renderservice.exception.TooManyRenditionsException;
import com.perceptivesoftware.renderservice.options.RenderOptions;
import com.saperion.cache.FileCache;
import com.saperion.cache.exception.CacheException;
import com.saperion.cache.exception.CorruptElementException;
import com.saperion.cache.exception.ElementNotFoundException;
import com.saperion.common.io.InputStreamDescriptor;
import com.saperion.common.io.TempFileByteBufferOutputStream;
import com.saperion.common.logging.Logger;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.renditions.Rendition;
import com.saperion.connector.renditions.exceptions.RenderingException;

/**
 * A RenditionCreator is used to create single or mutliple renditions based on a specified pair
 * of content and options.
 */
public final class RenditionCreator {

	private static final int MEGABYTE = 1000000;

	private static final Logger LOGGER = Logger.getLogger(RenditionCreator.class);

	private static FileCache<RenderServiceCacheKey> cache;

	static {
		try {
			cache = RenderServiceCacheManager.getInstance().getRenderCache();
		} catch (CacheException e) {
			cache = null;
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
		}
	}

	private MessageDigest digest;

	private InputStreamDescriptor input;
	private RenderOptions options;

	private String hash;

	private int bufferSize;
	private int tempsize;
	private int receiveSize;
	private int receiveSizeBytes;

	/**
	 * @param input
	 * 		content to render
	 * @param options
	 * 		options for render-engine
	 */
	public RenditionCreator(InputStreamDescriptor input, RenderOptions options) {
		this.input = input;
		this.options = options;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			digest = null;
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
		}

		RenderServiceConfiguration config = RenderServiceConfiguration.getInstance();

		this.bufferSize =
				config.getInt(ConfigurationKeys.MEMORY_BUFFERSIZE,
						ConfigurationDefaults.MEMORY_BUFFERSIZE);

		this.tempsize =
				config.getInt(ConfigurationKeys.MEMORY_TEMPSIZE,
						ConfigurationDefaults.MEMORY_TEMPSIZE);

		this.receiveSize =
				config.getInt(ConfigurationKeys.MEMORY_MAXRECEIVESIZE,
						ConfigurationDefaults.MEMORY_MAXRECEIVESIZE);

		this.receiveSizeBytes = receiveSize * MEGABYTE;
	}

	/**
	 * @return single rendition of the content
	 * @throws RenderingException
	 * 		exception when rendering the content
	 * @throws TimeoutException
	 * 		timeout reached while waiting for result from render engine
	 * @throws InterruptedException
	 * 		render-thread was interrupted
	 * @throws ExecutionException
	 * 		generic execution exception when running the render-job
	 * @throws IOException 
	 * 		IO error when reading content
	 * @throws ContentTooLargeException 
	 * 		when received content is larger than allowed maximum
	 */
	@TraceLog
	public Rendition getSingleRendition() throws RenderingException, TimeoutException,
			InterruptedException, ExecutionException, IOException, ContentTooLargeException {

		try (TempFileByteBufferOutputStream temporaryContent =
				new TempFileByteBufferOutputStream(tempsize)) {

			readInputAndCreateHash(temporaryContent);

			Rendition fromCache = getFromCache();

			if (fromCache != null) {
				return fromCache;
			}

			RenderJob job = new RenderJob(input, options);
			Future<List<Rendition>> future = RenderThreadPool.submitJob(job);

			try {
				int tOut = options.getTimeout();
				List<Rendition> list = future.get(tOut, TimeUnit.SECONDS);

				// we expect exactly one rendition, because we return the result as binary
				if (list == null || list.size() == 0) {
					throw LOGGER.logThrow(new RenderingException(
							"Render engine did not return a rendition."));
				} else if (list.size() > 1) {
					throw new TooManyRenditionsException();
				}

				Rendition rendition = list.get(0);
				Rendition toReturn = saveInCache(rendition);

				return toReturn;
			} catch (TimeoutException | InterruptedException | ExecutionException e) {
				future.cancel(true);
				LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
				throw e;
			}
		}
	}

	@TraceLog
	private void readInputAndCreateHash(TempFileByteBufferOutputStream temporaryContent)
			throws IOException, ContentTooLargeException {
		if (digest != null) {
			try (DigestInputStream digestIs = new DigestInputStream(input.getInputStream(), digest)) {

				byte[] buffer = new byte[bufferSize];
				int countAll = 0;
				int count = digestIs.read(buffer, 0, bufferSize);

				while (count != -1) {
					temporaryContent.write(buffer, 0, count);
					countAll += count;

					if (countAll > receiveSizeBytes) {
						break;
					}

					count = digestIs.read(buffer, 0, bufferSize);
				}

				if (countAll > receiveSizeBytes) {
					throw new ContentTooLargeException(
							"Received content is too large. Allowed maximum: " + receiveSize + "MB");
				}

				byte[] hashBytes = digestIs.getMessageDigest().digest();
				this.hash = Hex.encodeHexString(hashBytes);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Content hash created.", "hash", hash);
				}

				this.input =
						new InputStreamDescriptor(temporaryContent.getInputStream(), input.getUrl());
			} catch (IOException e) {
				LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
				throw e;
			}
		}
	}

	@TraceLog
	private Rendition getFromCache() {
		if (cache == null || hash == null) {
			return null;
		}

		RenderServiceCacheKey key = new RenderServiceCacheKey(hash, options);

		try {
			if (cache.contains(key)) {
				final InputStream stream = cache.getStream(key);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key found in cache.", "key", key);
				}

				// TODO how to find out if rendition is complete?
				InputStreamDescriptor streamDescr =
						new InputStreamDescriptor(stream, input.getUrl());
				return new CacheRendition(streamDescr, options.getTargetFormat(),
						options.getPage(), true);
			}
		} catch (CacheException | CorruptElementException e) {
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
		} catch (ElementNotFoundException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Key not found in cache.", "key", key);
			}
		}

		return null;
	}

	@TraceLog
	private Rendition saveInCache(final Rendition rendition) {
		if (cache == null || hash == null) {
			return rendition;
		}

		RenderServiceCacheKey key = new RenderServiceCacheKey(hash, options);

		try {
			InputStreamDescriptor descriptor = rendition.getStream();

			try (InputStream inputStream = descriptor.getInputStream()) {

				cache.putStream(key, inputStream);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key stored in cache.", "key", key);
				}

				InputStreamDescriptor streamDescr =
						new InputStreamDescriptor(cache.getStream(key), descriptor.getUrl());
				return new CacheRendition(streamDescr, rendition.getTarget(), rendition.getPage(),
						rendition.isComplete());
			}
			// CHECKSTYLE:OFF
		} catch (Exception e) {
			// CHECKSTYLE:ON
			LOGGER.logThrowableWithStacktrace(e.getMessage(), e);
			return rendition;
		}
	}

	/**
	 * A rendition that is read from cache.
	 */
	public static class CacheRendition implements Rendition {
		private final InputStreamDescriptor stream;
		private final TargetFormat target;
		private final int page;
		private final boolean complete;

		public CacheRendition(InputStreamDescriptor stream, TargetFormat target, int page,
				boolean complete) {
			this.stream = stream;
			this.target = target;
			this.page = page;
			this.complete = complete;
		}

		@Override
		public InputStreamDescriptor getStream() {
			return stream;
		}

		@Override
		public TargetFormat getTarget() {
			return target;
		}

		@Override
		public int getPage() {
			return page;
		}

		@Override
		public boolean isComplete() {
			return complete;
		}

	}

}
