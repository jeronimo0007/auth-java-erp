package br.tec.omny.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

@Service
public class RecaptchaService {
    
    @Value("${recaptcha.secret-key}")
    private String secretKey;
    
    @Value("${recaptcha.verify-url}")
    private String verifyUrl;
    
    @Value("${recaptcha.enabled}")
    private boolean enabled;
    
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    
    /**
     * Valida o token do reCAPTCHA
     * @param recaptchaToken Token do reCAPTCHA enviado pelo frontend
     * @param clientIp IP do cliente (opcional)
     * @return true se o reCAPTCHA for válido, false caso contrário
     */
    public boolean validateRecaptcha(String recaptchaToken, String clientIp) {
        // Se o reCAPTCHA estiver desabilitado, sempre retorna true
        if (!enabled) {
            return true;
        }
        
        // Se o token estiver vazio, retorna false
        if (recaptchaToken == null || recaptchaToken.trim().isEmpty()) {
            System.out.println("RecaptchaService: Token vazio ou null");
            return false;
        }
        
        System.out.println("RecaptchaService: Token recebido: " + recaptchaToken.substring(0, Math.min(20, recaptchaToken.length())) + "...");
        
        try {
            // Prepara os parâmetros para a requisição
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("secret", secretKey);
            formData.add("response", recaptchaToken);
            if (clientIp != null && !clientIp.trim().isEmpty()) {
                formData.add("remoteip", clientIp);
            }
            
            // Faz a requisição para o Google
            Map<String, Object> response = webClient.post()
                .uri(verifyUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(10))
                .block();
            
            // Verifica se a resposta indica sucesso
            if (response != null && response.containsKey("success")) {
                Boolean success = (Boolean) response.get("success");
                System.out.println("RecaptchaService: Resposta do Google - Success: " + success);
                if (response.containsKey("error-codes")) {
                    System.out.println("RecaptchaService: Error codes: " + response.get("error-codes"));
                }
                return success != null && success;
            }
            
            System.out.println("RecaptchaService: Resposta inválida do Google");
            return false;
            
        } catch (WebClientResponseException e) {
            System.err.println("Erro HTTP ao validar reCAPTCHA: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao validar reCAPTCHA: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida o token do reCAPTCHA (sem IP do cliente)
     * @param recaptchaToken Token do reCAPTCHA enviado pelo frontend
     * @return true se o reCAPTCHA for válido, false caso contrário
     */
    public boolean validateRecaptcha(String recaptchaToken) {
        return validateRecaptcha(recaptchaToken, null);
    }
    
    /**
     * Verifica se o reCAPTCHA está habilitado
     * @return true se habilitado, false caso contrário
     */
    public boolean isEnabled() {
        return enabled;
    }
}
