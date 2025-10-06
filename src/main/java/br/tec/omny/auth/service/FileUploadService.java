package br.tec.omny.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${storage.s3.endpoint}")
    private String endpoint;

    @Value("${storage.s3.region}")
    private String region;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Value("${storage.s3.public_base}")
    private String publicBase;

    @Value("${storage.s3.access_key}")
    private String accessKey;

    @Value("${storage.s3.secret_key}")
    private String secretKey;

    private S3Client buildClient() {
        return S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();
    }

    public String uploadFile(MultipartFile file, String keyPrefix) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        String normalizedPrefix = (keyPrefix != null && !keyPrefix.isBlank())
            ? keyPrefix.replaceAll("^/+|/+$", "") + "/"
            : "";
        String key = normalizedPrefix + filename;

        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .acl("public-read")
            .contentType(file.getContentType())
            .build();

        try (S3Client s3 = buildClient()) {
            s3.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        }

        return publicBase + "/" + key; // URL pública
    }

    public List<String> uploadFiles(List<MultipartFile> files, String keyPrefix) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files == null) return urls;
        for (MultipartFile file : files) {
            String url = uploadFile(file, keyPrefix);
            if (url != null) {
                urls.add(url);
            }
        }
        return urls;
    }

    public boolean deleteFile(String key) {
        // optional: could implement deletion in DO Spaces when needed
        return true;
    }
}
