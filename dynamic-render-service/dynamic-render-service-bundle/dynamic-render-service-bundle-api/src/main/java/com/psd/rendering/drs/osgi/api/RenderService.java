package com.psd.rendering.drs.osgi.api;

import com.saperion.connector.render.engine.RenderEngine;

public interface RenderService {
    String render(String arg);
    
    RenderEngine getRenderEngine();
}
