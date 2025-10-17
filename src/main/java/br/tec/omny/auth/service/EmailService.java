package br.tec.omny.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    
    @Value("${email.service.host}")
    private String emailServiceHost;
    
    @Value("${email.service.auth-token}")
    private String emailServiceAuthToken;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Envia email de boas-vindas para um usuário
     * @param contactId ID do contato
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendWelcomeEmail(Long contactId) {
        try {
            // URL do endpoint com contactId no path
            String url = emailServiceHost + "/api/email/send/welcome_email/" + contactId;
            
            System.out.println("EmailService: Enviando email de boas-vindas para contactId: " + contactId);
            System.out.println("EmailService: URL: " + url);
            System.out.println("EmailService: Host: " + emailServiceHost);
            System.out.println("EmailService: Token: " + (emailServiceAuthToken != null ? "Presente" : "Ausente"));
            
            // Headers seguindo o padrão do curl
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic YWRtaW46YWRtaW4xMjM=");
            headers.set("X-API-Token", emailServiceAuthToken);
            headers.set("authtoken", emailServiceAuthToken);
            headers.set("Cookie", "sp_session=c7d2538cbecc335e0f0f7586479f5aac");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
            headers.set("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
            headers.set("sec-ch-ua-mobile", "?0");
            headers.set("sec-ch-ua-platform", "\"Windows\"");
            headers.set("DNT", "1");
            headers.set("Referer", "");
            
            // Body vazio (contactId está no path)
            String jsonBody = "{}";
            
            System.out.println("EmailService: ContactId: " + contactId);
            System.out.println("EmailService: JSON Body: " + jsonBody);
            
            // Cria a entidade HTTP
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            // Faz a requisição POST com body explícito
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
            );
            
            System.out.println("EmailService: Status Code: " + response.getStatusCode());
            System.out.println("EmailService: Response Body: " + response.getBody());
            
            // Verifica se a resposta foi bem-sucedida (status 200-299)
            boolean success = response.getStatusCode().is2xxSuccessful();
            System.out.println("EmailService: Email enviado com sucesso: " + success);
            
            // Se não foi bem-sucedido, mostra mais detalhes
            if (!success) {
                System.out.println("EmailService: Erro detalhado - Status: " + response.getStatusCode() + ", Body: " + response.getBody());
            }
            
            return success;
            
        } catch (Exception e) {
            // Log do erro (você pode usar um logger aqui se preferir)
            System.err.println("Erro ao enviar email de boas-vindas para contactId " + contactId + ": " + e.getMessage());
            e.printStackTrace();
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
            
            // Headers seguindo o padrão do curl
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic YWRtaW46YWRtaW4xMjM=");
            headers.set("X-API-Token", emailServiceAuthToken);
            headers.set("authtoken", emailServiceAuthToken);
            headers.set("Cookie", "sp_session=c7d2538cbecc335e0f0f7586479f5aac");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
            headers.set("sec-ch-ua", "\"Google Chrome\";v=\"141\", \"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"141\"");
            headers.set("sec-ch-ua-mobile", "?0");
            headers.set("sec-ch-ua-platform", "\"Windows\"");
            headers.set("DNT", "1");
            headers.set("Referer", "");
            
            // Body da requisição em formato JSON (sem encoding)
            String jsonBody = "{\"email\":\"" + email + "\"}";
            
            System.out.println("EmailService: Email original: " + email);
            System.out.println("EmailService: JSON Body: " + jsonBody);
            System.out.println("EmailService: JSON Body bytes: " + java.util.Arrays.toString(jsonBody.getBytes()));
            
            // Cria a entidade HTTP
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            // Faz a requisição POST com body explícito
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
            );
            
            // Verifica se a resposta foi bem-sucedida (status 200-299)
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            // Log do erro (você pode usar um logger aqui se preferir)
            System.err.println("Erro ao enviar email de verificação para " + email + ": " + e.getMessage());
            return false;
        }
    }
}
