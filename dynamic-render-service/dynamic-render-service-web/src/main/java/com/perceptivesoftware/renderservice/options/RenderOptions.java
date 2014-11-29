package com.perceptivesoftware.renderservice.options;

import com.saperion.common.lang.format.ToStringFormatter;
import com.saperion.connector.formats.TargetFormat;

/**
 * Holds options needed to render.
 */
public class RenderOptions {

	private final TargetFormat targetFormat;
	private final int page;
	private final int dpi;
	private final int height;
	private final int width;
	private final int timeout;

	/**
	 * @param targetFormat
	 * 		format to render to
	 * @param page
	 * 		page to render or -1 for all pages
	 * @param dpi
	 * 		DPI for rendition
	 * @param timeout
	 * 		maximum time to wait for rendition result
	 * @param height
	 * 		height of the rendition, if the rendition is an image
	 * @param width
	 * 		width of the rendition, if the rendition is an image
	 */
	public RenderOptions(TargetFormat targetFormat, int page, int dpi, int timeout, int height,
			int width) {
		this.targetFormat = targetFormat;
		this.page = page;
		this.dpi = dpi;
		this.height = height;
		this.width = width;
		this.timeout = timeout;
	}

	/**
	 * @param targetFormat
	 * 		format to render to
	 * @param page
	 * 		page to render or -1 for all pages
	 * @param dpi
	 * 		DPI for rendition
	 * @param timeout
	 * 		maximum time to wait for rendition result
	 */
	public RenderOptions(TargetFormat targetFormat, int page, int dpi, int timeout) {
		this.targetFormat = targetFormat;
		this.page = page;
		this.dpi = dpi;
		this.height = -1;
		this.width = -1;
		this.timeout = timeout;
	}

	/**
	 * @return format to render to
	 */
	public TargetFormat getTargetFormat() {
		return targetFormat;
	}

	/**
	 * @return page to render or -1 for all pages
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @return DPI for rendition
	 */
	public int getDpi() {
		return dpi;
	}

	/**
	 * @return desired height of rendition
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return desired width of rendition
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return maximum time to wait for result
	 */
	public int getTimeout() {
		return timeout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + targetFormat.getMimetype().hashCode();
		result = prime * result + page;
		result = prime * result + dpi;
		result = prime * result + width;
		result = prime * result + height;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RenderOptions) {
			RenderOptions other = (RenderOptions) obj;
			return targetFormat.equals(other.targetFormat) && dpi == other.dpi
					&& timeout == other.timeout && page == other.page && height == other.height
					&& width == other.width;
		}

		return false;
	}

	@Override
	public String toString() {
		return ToStringFormatter.format(getClass(), null, "targetFormat", targetFormat, "dpi", dpi,
				"page", page, "height", height, "width", width, "timeout", timeout);
	}

}
