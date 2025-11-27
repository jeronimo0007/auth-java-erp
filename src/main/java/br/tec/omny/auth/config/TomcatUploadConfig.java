package br.tec.omny.auth.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

@Configuration
public class TomcatUploadConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                super.postProcessContext(context);
                disableFileCountLimit();
            }
        };
    }

    private void disableFileCountLimit() {
        try {
            Field field = FileUploadBase.class.getDeclaredField("fileCountMax");
            field.setAccessible(true);
            field.setLong(null, -1L);
        } catch (Exception ignored) {
        }
    }
}

