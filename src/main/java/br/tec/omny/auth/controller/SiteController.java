package br.tec.omny.auth.controller;

import br.tec.omny.auth.dto.ApiResponse;
import br.tec.omny.auth.dto.SiteCreationResult;
import br.tec.omny.auth.dto.SiteRegisterRequest;
import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.entity.Site;
import br.tec.omny.auth.entity.SiteImage;
import br.tec.omny.auth.service.AuthService;
import br.tec.omny.auth.service.SiteImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class SiteController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private SiteImageService siteImageService;

    /**
     * Endpoint para registro de site
     * POST /register/site
     */
    @PostMapping(value = "/register/site", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> registerSite(
            @RequestBody SiteRegisterRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            applyRequestDefaults(request);
            populateClientMetadata(request, httpRequest);
            
            SiteCreationResult result = authService.registerSite(request);
            Client client = result.getClient();
            Site site = result.getSite();
            
            // Remove dados sensíveis da resposta
            client.setActive(null);
            client.setDefaultClient(null);
            
            java.util.Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("client", client);
            responseData.put("siteId", site != null ? site.getSiteId() : null);
            return ResponseEntity.ok(ApiResponse.success("Site registrado com sucesso! Cliente, projeto e 3 tasks foram criadas (Desenvolvimento, Configurações e Fatura).", responseData));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao registrar site: " + e.getMessage()));
        }
    }

    private void applyRequestDefaults(SiteRegisterRequest request) {
        if (!StringUtils.hasText(request.getCompany())) {
            String fallback = combineNames(request.getFirstname(), request.getLastname());
            if (StringUtils.hasText(fallback)) {
                request.setCompany(fallback);
            }
        }

        if (!StringUtils.hasText(request.getNomeSite())) {
            String fallback = combineNames(request.getFirstname(), request.getLastname());
            if (StringUtils.hasText(fallback)) {
                request.setNomeSite(fallback);
            }
        }
    }

    private String combineNames(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(firstName)) {
            builder.append(firstName.trim());
        }
        if (StringUtils.hasText(lastName)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(lastName.trim());
        }
        return builder.toString().trim();
    }

    private void populateClientMetadata(SiteRegisterRequest request, HttpServletRequest httpRequest) {
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        String clientIp = null;
        if (StringUtils.hasText(xForwardedFor)) {
            clientIp = xForwardedFor.split(",")[0].trim();
        } else {
            clientIp = httpRequest.getRemoteAddr();
        }
        request.setClientIp(clientIp);
        request.setUserAgent(httpRequest.getHeader("User-Agent"));
    }

    @PostMapping("/site/{siteId}/images")
    public ResponseEntity<ApiResponse> uploadSiteImages(
            @PathVariable Integer siteId,
            MultipartHttpServletRequest request) {
        try {
            List<SiteImageService.SiteImageUpload> uploads = new ArrayList<>();
            Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String fieldName = fileNames.next();
                List<MultipartFile> fieldFiles = request.getFiles(fieldName);
                if (fieldFiles == null || fieldFiles.isEmpty()) {
                    continue;
                }
                int size = fieldFiles.size();
                for (int index = 0; index < size; index++) {
                    MultipartFile file = fieldFiles.get(index);
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    String imageName = size > 1 ? fieldName + "_" + (index + 1) : fieldName;
                    uploads.add(new SiteImageService.SiteImageUpload(file, imageName));
                }
            }
            List<SiteImage> saved = siteImageService.uploadSiteImages(siteId, uploads);
            return ResponseEntity.ok(ApiResponse.success("Imagens salvas com sucesso", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Erro ao salvar imagens: " + e.getMessage()));
        }
    }
}