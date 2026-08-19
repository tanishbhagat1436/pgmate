package com.PGMATE.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirects root "/" directly to your Frontend folder's index.html
        registry.addRedirectViewController("/", "/Frontend/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps all static paths including Frontend subfolder
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/static/Frontend/");
    }
}
