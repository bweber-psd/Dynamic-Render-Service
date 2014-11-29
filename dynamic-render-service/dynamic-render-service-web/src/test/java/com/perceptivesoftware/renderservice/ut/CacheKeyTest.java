package com.perceptivesoftware.renderservice.ut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.perceptivesoftware.renderservice.cache.RenderServiceCacheKey;
import com.perceptivesoftware.renderservice.options.RenderOptions;
import com.saperion.connector.options.Targets;

/**
 * Tests if cache keys differ or equal when they should.
 */
@RunWith(Parameterized.class)
public class CacheKeyTest {

	private final String hashCode1;
	private final String hashCode2;
	private final RenderOptions options1;
	private final RenderOptions options2;
	private final boolean different;

	public CacheKeyTest(String hashCode1, RenderOptions options1, String hashCode2,
			RenderOptions options2, boolean different) {
		this.hashCode1 = hashCode1;
		this.hashCode2 = hashCode2;
		this.options1 = options1;
		this.options2 = options2;
		this.different = different;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> parameters() {
		List<Object[]> args = new ArrayList<>();

		String hash1 = UUID.randomUUID().toString();
		String hash2 = UUID.randomUUID().toString();

		// options differ by page
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10), hash1,
				new RenderOptions(Targets.PDF, 2, 200, 10), true });

		// options differ by target
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10), hash1,
				new RenderOptions(Targets.PNG, 1, 200, 10), true });

		// options differ by dpi
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10), hash1,
				new RenderOptions(Targets.PDF, 1, 300, 10), true });

		// options differ by hash
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10), hash2,
				new RenderOptions(Targets.PDF, 1, 200, 10), true });

		// options differ by size
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10, 600, 800), hash1,
				new RenderOptions(Targets.PDF, 1, 200, 10, 300, 400), true });

		// same options
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10, 600, 800), hash1,
				new RenderOptions(Targets.PDF, 1, 200, 10, 600, 800), false });

		// same but timeout
		args.add(new Object[] { hash1, new RenderOptions(Targets.PDF, 1, 200, 10, 600, 800), hash1,
				new RenderOptions(Targets.PDF, 1, 200, 20, 600, 800), false });

		return args;
	}

	@Test
	public void cacheKeysAreDifferent() throws Exception {
		RenderServiceCacheKey key1 = new RenderServiceCacheKey(hashCode1, options1);
		RenderServiceCacheKey key2 = new RenderServiceCacheKey(hashCode2, options2);

		if (different) {
			Assert.assertFalse(key1.equals(key2));
			Assert.assertFalse(key1.hashCode() == key2.hashCode());
		} else {
			Assert.assertEquals(key1, key2);
			Assert.assertEquals(key1.hashCode(), key2.hashCode());
		}
	}
}
