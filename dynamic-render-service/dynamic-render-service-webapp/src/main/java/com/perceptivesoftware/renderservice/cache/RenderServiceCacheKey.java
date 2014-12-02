package com.perceptivesoftware.renderservice.cache;

import java.io.Serializable;

import com.perceptivesoftware.renderservice.options.RenderOptions;

/**
 * Key for the render service cache. Uses the content-hash, the page number and the
 * target-format to distinguish between different renditions.
 */
public class RenderServiceCacheKey implements Serializable {

	private static final long serialVersionUID = 1142011782225711601L;

	private final String contentHash;
	private final int optionsHash;

	/**
	 * @param hash
	 * 		content-hash
	 * @param options
	 * 		render options
	 */
	public RenderServiceCacheKey(String hash, RenderOptions options) {
		this.contentHash = hash;
		this.optionsHash = options.hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + contentHash.hashCode();
		result = prime * result + optionsHash;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RenderServiceCacheKey) {
			RenderServiceCacheKey other = (RenderServiceCacheKey) obj;

			return contentHash.equals(other.contentHash) && optionsHash == other.optionsHash;
		}

		return false;
	}

	@Override
	public String toString() {
		// ToStringFormatter not used on purpose because string was too long to be used as filename
		return contentHash + ", " + optionsHash;
	}

}
