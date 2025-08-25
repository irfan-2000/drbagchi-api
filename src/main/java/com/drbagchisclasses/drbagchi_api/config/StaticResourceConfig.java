package com.drbagchisclasses.drbagchi_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${GlobalUploadPath}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // Map /media/** to your physical upload path
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
