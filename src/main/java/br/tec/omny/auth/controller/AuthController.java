package br.tec.omny.auth.controller;

import br.tec.omny.auth.dto.AdminLoginRequest;
import br.tec.omny.auth.dto.AdminLoginResponse;
import br.tec.omny.auth.dto.ApiResponse;
import br.tec.omny.auth.dto.ClientInfoResponse;
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
            
            // Cria um objeto de resposta com o email incluído
            java.util.Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("userId", client.getUserId());
            responseData.put("company", client.getCompany());
            responseData.put("phoneNumber", client.getPhoneNumber());
            responseData.put("email", request.getEmail());
            responseData.put("zip", request.getZip());
            responseData.put("city", client.getCity());
            responseData.put("state", request.getState());
            responseData.put("address", client.getAddress());
            responseData.put("dateCreated", client.getDateCreated());
            
            return ResponseEntity.ok(ApiResponse.success("Usuário registrado com sucesso", responseData));
            
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
    
    /**
     * Endpoint para buscar informações básicas de um cliente
     * GET /auth/client/{id}
     */
    @GetMapping("/client/{id}")
    public ResponseEntity<ApiResponse> getClientInfo(@PathVariable Long id) {
        try {
            ClientInfoResponse clientInfo = authService.getClientInfo(id);
            
            return ResponseEntity.ok(ApiResponse.success("Informações do cliente encontradas", clientInfo));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Endpoint para contar quantos sites um cliente possui
     * GET /auth/client/{id}/sites/count
     */
    @GetMapping("/client/{id}/sites/count")
    public ResponseEntity<ApiResponse> getClientSitesCount(@PathVariable Long id) {
        try {
            long sitesCount = authService.getClientSitesCount(id);
            
            return ResponseEntity.ok(ApiResponse.success("Contagem de sites obtida com sucesso", 
                java.util.Map.of("clientId", id, "sitesCount", sitesCount)));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
}

