package com.psd.rendering.drs.osgi.service;

import com.psd.rendering.drs.osgi.api.RenderService;


public class RenderServiceImpl implements RenderService {
    @Override
    public String render(String arg) {
        return new StringBuilder(arg).reverse().toString();
    }
}
