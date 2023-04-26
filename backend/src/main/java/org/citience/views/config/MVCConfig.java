package org.citience.views.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/network").setViewName("network/index");
        registry.addViewController("/network/settings").setViewName("network/settings");
        registry.addViewController("/data").setViewName("data/index");
        registry.addViewController("/data/updateInfo").setViewName("data/infoSettings");
        registry.addViewController("/about").setViewName("about");
    }
}
