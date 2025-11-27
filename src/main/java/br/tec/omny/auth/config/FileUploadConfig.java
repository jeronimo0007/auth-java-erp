package br.tec.omny.auth.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfig {

    static {
        System.setProperty("org.apache.tomcat.util.http.fileupload.FileUploadBase.fileCountMax", "-1");
    }
}

