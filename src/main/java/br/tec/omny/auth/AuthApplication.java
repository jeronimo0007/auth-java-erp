package br.tec.omny.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.util.http.fileupload.FileUploadBase.fileCountMax", "-1");
		System.setProperty("org.apache.commons.fileupload.FileUploadBase.fileCountMax", "-1");
		SpringApplication.run(AuthApplication.class, args);
	}

}
