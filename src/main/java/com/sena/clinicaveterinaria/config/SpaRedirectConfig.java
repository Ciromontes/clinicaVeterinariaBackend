package com.sena.clinicaveterinaria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaRedirectConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // raíz
        registry.addViewController("/").setViewName("forward:/index.html");
        // rutas de un nivel que no sean /api y sin punto (evita recursos estáticos)
        registry.addViewController("/{path:^(?!api$)[^\\.]*}")
                .setViewName("forward:/index.html");
        // rutas de dos niveles
        registry.addViewController("/{path:^(?!api$)[^\\.]*}/{path2:[^\\.]*}")
                .setViewName("forward:/index.html");
    }
}