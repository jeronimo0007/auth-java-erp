package br.tec.omny.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/auth/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/auth/lost_password")
                .addPathPatterns("/register/site");
    }
}
