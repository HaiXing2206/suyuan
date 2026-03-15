package org.Tracing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        registry.addViewController("/").setViewName("redirect:/html/index.html");
        registry.addViewController("/users").setViewName("redirect:/html/users.html");
        registry.addViewController("/products").setViewName("redirect:/html/products.html");
        registry.addViewController("/analysis").setViewName("redirect:/html/analysis.html");
        registry.addViewController("/tasks").setViewName("redirect:/html/tasks.html");
        registry.addViewController("/settings").setViewName("redirect:/html/settings.html");
        
        registry.addViewController("/error").setViewName("redirect:/error.html");
    }
} 