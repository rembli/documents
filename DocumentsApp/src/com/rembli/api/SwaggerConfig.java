package com.rembli.api;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerConfig extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("A simple API for documents");
        beanConfig.setVersion("1");
        //beanConfig.setSchemes(new String[]{"https"});
        beanConfig.setSchemes(new String[]{"http"});        
        beanConfig.setBasePath("/documents/api");
        beanConfig.setResourcePackage("com.rembli.api");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
    }
}
