package br.tec.omny.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
public class EmailService {
    
    @Value("${email.service.host}")
    private String emailServiceHost;
    
    @Value("${email.service.auth-token}")
    private String emailServiceAuthToken;
    
    @Value("${email.service.basic-auth}")
    private String emailServiceBasicAuth;

    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    
    /**
     * Envia email de boas-vindas para um usuário
     * @param contactId ID do contato
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendWelcomeEmail(Long contactId) {
        try {
            // URL do endpoint com contactId no path
            String url = emailServiceHost + "/api/email/send/welcome_email/" + contactId;
            
            // Body vazio (contactId está no path)
            String jsonBody = "{}";
            
            // Faz a requisição POST usando WebClient
            webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + emailServiceBasicAuth)
                .header("X-API-Token", emailServiceAuthToken)
                .header("authtoken", emailServiceAuthToken)
                .header("Cookie", "sp_session=c7d2538cbecc335e0f0f7586479f5aac")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("DNT", "1")
                .header("Referer", "")
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            return true;

        } catch (WebClientResponseException e) {
            System.err.println("Erro HTTP ao enviar email de boas-vindas para contactId " + contactId + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de boas-vindas para contactId " + contactId + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Envia email de verificação para um usuário
     * @param email Email do usuário
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendVerificationEmail(String email) {
        try {
            // URL do endpoint
            String url = emailServiceHost + "/api/email/send/verification_email";
            
            // Body da requisição em formato JSON
            String jsonBody = "{\"email\":\"" + email + "\"}";
            
            // Faz a requisição POST usando WebClient
            webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + emailServiceBasicAuth)
                .header("X-API-Token", emailServiceAuthToken)
                .header("authtoken", emailServiceAuthToken)
                .header("Cookie", "sp_session=c7d2538cbecc335e0f0f7586479f5aac")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("DNT", "1")
                .header("Referer", "")
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
            
            return true;
            
        } catch (WebClientResponseException e) {
            System.err.println("Erro HTTP ao enviar email de verificação para " + email + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de verificação para " + email + ": " + e.getMessage());
            return false;
        }
    }
}