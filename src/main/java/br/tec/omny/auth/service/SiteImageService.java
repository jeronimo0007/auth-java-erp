package br.tec.omny.auth.service;

import br.tec.omny.auth.entity.SiteImage;
import br.tec.omny.auth.repository.SiteImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteImageService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SiteImageRepository siteImageRepository;

    public List<SiteImage> uploadSiteImages(Integer siteId, List<MultipartFile> images) throws IOException {
        List<SiteImage> saved = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return saved;
        }
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }

            String keyPrefix = "erp/sites/" + siteId + "/site-images";
            String url = fileUploadService.uploadFile(image, keyPrefix);
            if (url == null) {
                continue;
            }

            SiteImage entity = new SiteImage();
            entity.setSiteId(siteId);
            entity.setUrl(url);
            entity.setFilename(image.getOriginalFilename());
            saved.add(siteImageRepository.save(entity));
        }

        return saved;
    }
}

