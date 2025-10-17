package br.tec.omny.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Value("${api.auth.basic.username}")
    private String basicUsername;
    
    @Value("${api.auth.basic.password}")
    private String basicPassword;
    
    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request, 
                           @org.springframework.lang.NonNull HttpServletResponse response, 
                           @org.springframework.lang.NonNull Object handler) throws Exception {
        // Log para debug
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("AuthInterceptor: Interceptando requisição " + method + " para: " + requestURI);
        
        // Permite requisições OPTIONS (preflight CORS)
        if ("OPTIONS".equals(method)) {
            System.out.println("AuthInterceptor: Requisição OPTIONS (preflight), permitindo acesso");
            return true;
        }
        
        // Pula validação para endpoints públicos (login, register)
        if (requestURI.equals("/auth/login") || requestURI.equals("/auth/register") || 
            requestURI.equals("/register/site") || requestURI.equals("/auth/lost_password")) {
            System.out.println("AuthInterceptor: Endpoint público, permitindo acesso");
            return true;
        }
        
        // Valida Basic Auth
        String authHeader = request.getHeader("Authorization");
        System.out.println("AuthInterceptor: Authorization header: " + (authHeader != null ? "Presente" : "Ausente"));
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);
            
            System.out.println("AuthInterceptor: Credenciais decodificadas: " + values[0] + ":" + (values.length > 1 ? "***" : ""));
            
            if (values.length == 2 && values[0].equals(basicUsername) && values[1].equals(basicPassword)) {
                System.out.println("AuthInterceptor: Basic Auth válido, permitindo acesso");
                return true;
            } else {
                System.out.println("AuthInterceptor: Basic Auth inválido");
            }
        }
        
        System.out.println("AuthInterceptor: Autenticação necessária");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Autenticação necessária\"}");
        return false;
    }
}
