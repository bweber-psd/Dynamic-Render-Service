package com.perceptivesoftware.renderservice.ut;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.perceptivesoftware.renderservice.cache.RenderServiceCacheManager;
import com.perceptivesoftware.renderservice.configuration.ConfigurationKeys;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.perceptivesoftware.renderservice.exception.ContentTooLargeException;
import com.perceptivesoftware.renderservice.options.RenderOptions;
import com.perceptivesoftware.renderservice.render.RenditionCreator;
import com.saperion.common.io.InputStreamDescriptor;
import com.saperion.common.test.RandomInputStream;
import com.saperion.connector.options.Targets;

/**
 * Tests if the size of the content sent by the client is limited.
 */
public class MaxReceiveSizeTest {

	public static class ConfigurationMock extends MockUp<RenderServiceConfiguration> {
		@Mock
		public int getInt(String name, int defaultValue) {
			if (name.equalsIgnoreCase(ConfigurationKeys.MEMORY_MAXRECEIVESIZE)) {
				return 1;
			}

			return defaultValue;
		}
	}

	private ConfigurationMock configurationMock;

	@Before
	public void setup() {
		configurationMock = new ConfigurationMock();
		RenderServiceConfiguration.initialize(".");
	}

	@After
	public void cleanup() {
		configurationMock.tearDown();
	}

	@Test(expected = ContentTooLargeException.class)
	public void contentTooLargeExceptionThrown(@Mocked final RenderServiceCacheManager cacheManager)
			throws Exception {

		new NonStrictExpectations() {
			{
				RenderServiceCacheManager.getInstance();
				returns(cacheManager);
				cacheManager.getRenderCache();
				returns(null);
			}
		};

		InputStreamDescriptor input = new InputStreamDescriptor(new RandomInputStream(2000000));
		RenderOptions options = new RenderOptions(Targets.PDF, 1, 200, 10);
		new RenditionCreator(input, options).getSingleRendition();
	}
}
