package com.psd.rendering.openrender.oorender.module;

import com.psd.rendering.openrender.api.module.RenderModule;
import com.psd.rendering.openrender.api.module.RenderModuleOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Burkhard
 * Date: 16.11.14
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class OORenderModuleOptions implements RenderModuleOptions {

    private RenderModule renderModule;


    public OORenderModuleOptions(RenderModule renderModule) {
        this.renderModule = renderModule;
    }
}
