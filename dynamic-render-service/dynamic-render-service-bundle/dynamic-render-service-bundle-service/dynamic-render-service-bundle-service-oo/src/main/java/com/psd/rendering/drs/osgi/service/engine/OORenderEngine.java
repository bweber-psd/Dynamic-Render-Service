package com.psd.rendering.drs.osgi.service.engine;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.saperion.common.io.InputStreamDescriptor;
import com.saperion.connector.formats.SourceFormat;
import com.saperion.connector.formats.TargetFormat;
import com.saperion.connector.options.Options;
import com.saperion.connector.render.engine.BaseRenderEngine;
import com.saperion.connector.renditions.Rendition;
import com.saperion.connector.renditions.exceptions.RenderingException;

public class OORenderEngine extends BaseRenderEngine {

	@Override
	public Set<SourceFormat> getSupportedSourceFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<TargetFormat> getSupportedTargetFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Rendition> safeRender(InputStreamDescriptor arg0, SourceFormat arg1, Options arg2)
			throws TimeoutException, RenderingException {
		// TODO Auto-generated method stub
		return null;
	}

}
