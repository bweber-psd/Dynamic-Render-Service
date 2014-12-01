package com.psd.rendering.drs.osgi.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.psd.rendering.drs.osgi.api.RenderServiceTracker;

public class RenderServiceTrackerImpl implements RenderServiceTracker {

	private static List<RenderService> serviceList = Collections.synchronizedList(new ArrayList<RenderService>());
	
	@Override
	public boolean addService(RenderService renderService) {
		return serviceList.add(renderService);
		
	}

	@Override
	public boolean removeService(RenderService renderService) {
		return serviceList.remove(renderService);
		
	}

	@Override
	public RenderService getService(String rule) {
		return serviceList.get(0);
	}

}
