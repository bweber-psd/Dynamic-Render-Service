package com.psd.rendering.drs.osgi.api;

public interface RenderServiceTracker {

	boolean addService(RenderService renderService);
	boolean removeService(RenderService renderService);
	RenderService getService(String rule);
}
