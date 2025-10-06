package br.tec.omny.auth.controller;

import br.tec.omny.auth.dto.AdminLoginRequest;
import br.tec.omny.auth.dto.AdminLoginResponse;
import br.tec.omny.auth.dto.ApiResponse;
import br.tec.omny.auth.dto.LoginRequest;
import br.tec.omny.auth.dto.LostPasswordRequest;
import br.tec.omny.auth.dto.RegisterRequest;
import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.entity.Contact;
import br.tec.omny.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Endpoint de teste
     * GET /auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoint funcionando!");
    }
    
    /**
     * Endpoint de teste POST
     * POST /auth/test-post
     */
    @PostMapping("/test-post")
    public ResponseEntity<String> testPost() {
        return ResponseEntity.ok("Auth POST endpoint funcionando!");
    }
    
    /**
     * Endpoint de teste POST com JSON
     * POST /auth/test-json
     */
    @PostMapping(value = "/test-json", consumes = "application/json")
    public ResponseEntity<String> testJson(@RequestBody String body) {
        return ResponseEntity.ok("Auth JSON endpoint funcionando! Body: " + body);
    }
    
    /**
     * Endpoint de teste de login
     * POST /auth/test-login
     */
    @PostMapping(value = "/test-login", consumes = "application/json")
    public ResponseEntity<String> testLogin(@RequestBody String body) {
        return ResponseEntity.ok("Auth login endpoint funcionando! Body: " + body);
    }
    
    /**
     * Endpoint para registro de usuário
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {
            Client client = authService.register(request);
            
            // Remove dados sensíveis da resposta
            client.setActive(null);
            client.setDefaultClient(null);
            
            return ResponseEntity.ok(ApiResponse.success("Usuário registrado com sucesso", client));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Endpoint para login
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            Contact contact = authService.login(request);
            
            // Remove dados sensíveis da resposta
            contact.setPassword(null);
            
            return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", contact));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Endpoint para recuperação de senha
     * POST /api/auth/lost_password
     */
    @PostMapping("/lost_password")
    public ResponseEntity<ApiResponse> lostPassword(@RequestBody LostPasswordRequest request) {
        try {
            boolean emailExists = authService.initiatePasswordRecovery(request.getEmail());
            
            if (emailExists) {
                return ResponseEntity.ok(ApiResponse.success(
                    "Se o email estiver cadastrado, você receberá instruções para redefinir sua senha"));
            } else {
                return ResponseEntity.ok(ApiResponse.success(
                    "Se o email estiver cadastrado, você receberá instruções para redefinir sua senha"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao processar solicitação de recuperação de senha"));
        }
    }
    
    /**
     * Endpoint para login de administrador/staff
     * POST /api/auth/admin/login
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        try {
            AdminLoginResponse response = authService.adminLogin(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AdminLoginResponse.error("Erro interno do servidor: " + e.getMessage()));
        }
    }
    
}

