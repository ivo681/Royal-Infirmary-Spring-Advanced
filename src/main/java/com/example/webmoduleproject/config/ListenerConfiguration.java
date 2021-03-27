package com.example.webmoduleproject.config;


import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ListenerConfiguration implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new RequestContextListener());
    }
}
