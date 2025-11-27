package br.tec.omny.auth.controller;

import br.tec.omny.auth.dto.ApiResponse;
import br.tec.omny.auth.dto.SiteRegisterRequest;
import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class SiteController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Endpoint para registro de site
     * POST /register/site
     */
    @PostMapping("/register/site")
    public ResponseEntity<ApiResponse> registerSite(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "nome_site", required = false) String nomeSite,
            @RequestParam(value = "dominio", required = false) String dominio,
            @RequestParam(value = "descricao_negocio", required = false) String descricaoNegocio,
            @RequestParam(value = "publico_alvo", required = false) String publicoAlvo,
            @RequestParam(value = "banner_texto", required = false) String bannerTexto,
            @RequestParam(value = "banner_texto_img", required = false) MultipartFile bannerTextoImg,
            
            @RequestParam(value = "tipo_site", required = false) String tipoSite,
            @RequestParam(value = "quem_somos", required = false) String quemSomos,
            @RequestParam(value = "empresa_imagem", required = false) MultipartFile empresaImagem,
            @RequestParam(value = "servicos", required = false) String servicos,
            @RequestParam(value = "servicos_imagens[]", required = false) MultipartFile[] servicosImagens,
            @RequestParam(value = "logo_opcao", required = false) String logoOpcao,
            @RequestParam(value = "email_desejado", required = false) String emailDesejado,
            @RequestParam(value = "banner_opcao", required = false) String bannerOpcao,
            @RequestParam(value = "banner_ia_descricao", required = false) String bannerIaDescricao,
            
            @RequestParam(value = "banner_profissional_descricao", required = false) String bannerProfissionalDescricao,
            @RequestParam(value = "email_empresa", required = false) String emailEmpresa,
            @RequestParam(value = "telefone_empresa", required = false) String telefoneEmpresa,
            @RequestParam(value = "endereco_empresa", required = false) String enderecoEmpresa,
            @RequestParam(value = "secao1_titulo", required = false) String secao1Titulo,
            @RequestParam(value = "secao1_conteudo", required = false) String secao1Conteudo,
            @RequestParam(value = "secao2_titulo", required = false) String secao2Titulo,
            @RequestParam(value = "secao2_conteudo", required = false) String secao2Conteudo,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "favicon", required = false) MultipartFile favicon,
            @RequestParam(value = "cor_principal", required = false) String corPrincipal,
            @RequestParam(value = "cor_secundaria", required = false) String corSecundaria,
            @RequestParam(value = "estilo", required = false) String estilo,
            @RequestParam(value = "observacoes", required = false) String observacoes,
            @RequestParam(value = "firstname", required = false) String firstname,
            @RequestParam(value = "lastname", required = false) String lastname,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "facebook", required = false) String facebook,
            @RequestParam(value = "linkedin", required = false) String linkedin,
            @RequestParam(value = "youtube", required = false) String youtube,
            @RequestParam(value = "tiktok", required = false) String tiktok,
            @RequestParam(value = "instagram", required = false) String instagram,
            
            // Novos campos
            @RequestParam(value = "preference", required = false) String preference,
            @RequestParam(value = "description_site", required = false) String descriptionSite,
            @RequestParam(value = "type_site", required = false) String typeSite,
            @RequestParam(value = "user_id", required = false) String userIdStr,
            @RequestParam(value = "recaptchaToken", required = false) String recaptchaToken,
            @RequestParam(value = "ia", required = false) Boolean ia,
            @RequestParam(value = "product_id", required = false) String productId,
            @RequestParam(value = "afm", required = false) String afm,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "type", required = false) String type,
            HttpServletRequest httpRequest) {
        
        try {
            // Cria o objeto de request
            SiteRegisterRequest request = new SiteRegisterRequest();
            request.setEmail(email);
            // Usa phoneNumber se disponível, senão usa phonenumber
            request.setPhonenumber(phoneNumber != null ? phoneNumber : phonenumber);

            if(company == null){
                request.setCompany(firstName+" "+lastName);
            }else {
                request.setCompany(company);
            }

            if(nomeSite == null){
                request.setNomeSite(firstName+" "+lastName);
            }else {
                request.setNomeSite(nomeSite);
            }

            request.setDominio(dominio);
            request.setDescricaoNegocio(descricaoNegocio);
            request.setPublicoAlvo(publicoAlvo);
            request.setBannerTexto(bannerTexto);
            request.setBannerTextoImg(bannerTextoImg);
            
            request.setTipoSite(tipoSite);
            request.setQuemSomos(quemSomos);
            request.setEmpresaImagem(empresaImagem);
            request.setServicos(servicos);
            if (servicosImagens != null) {
                request.setServicosImagens(java.util.Arrays.asList(servicosImagens));
            }
            request.setLogoOpcao(logoOpcao);
            request.setEmailDesejado(emailDesejado);
            request.setBannerOpcao(bannerOpcao);
            
            request.setBannerIaDescricao(bannerIaDescricao);
            
            request.setBannerProfissionalDescricao(bannerProfissionalDescricao);
            request.setEmailEmpresa(emailEmpresa);
            request.setTelefoneEmpresa(telefoneEmpresa);
            request.setEnderecoEmpresa(enderecoEmpresa);
            request.setSecao1Titulo(secao1Titulo);
            request.setSecao1Conteudo(secao1Conteudo);
            request.setSecao2Titulo(secao2Titulo);
            request.setSecao2Conteudo(secao2Conteudo);
            request.setLogo(logo);
            request.setFavicon(favicon);
            request.setCorPrincipal(corPrincipal);
            request.setCorSecundaria(corSecundaria);
            request.setEstilo(estilo);
            request.setObservacoes(observacoes);
            // Usa firstName/LastName se disponível, senão usa firstname/lastname
            request.setFirstname(firstName != null ? firstName : firstname);
            request.setLastname(lastName != null ? lastName : lastname);
            request.setFacebook(facebook);
            request.setLinkedin(linkedin);
            request.setYoutube(youtube);
            request.setTiktok(tiktok);
            request.setInstagram(instagram);
            
            // Novos campos
            request.setPreference(preference);
            request.setDescriptionSite(descriptionSite);
            request.setTypeSite(typeSite);
            
            // Conversão segura do user_id
            Long userId = null;
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                try {
                    userId = Long.parseLong(userIdStr.trim());
                } catch (NumberFormatException e) {
                    // Se não conseguir converter, define como null (será tratado como novo cliente)
                    userId = null;
                }
            }
            request.setUserId(userId);
            request.setPassword(password);
            request.setRecaptchaToken(recaptchaToken);
            request.setIa(ia);
            request.setProductId(productId);
            request.setAfm(afm);
            request.setMessage(message);
            request.setType(type);
            String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
            String clientIp = null;
            if (xForwardedFor != null && !xForwardedFor.trim().isEmpty()) {
                clientIp = xForwardedFor.split(",")[0].trim();
            } else {
                clientIp = httpRequest.getRemoteAddr();
            }
            request.setClientIp(clientIp);
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            
            Client client = authService.registerSite(request);
            
            // Remove dados sensíveis da resposta
            client.setActive(null);
            client.setDefaultClient(null);
            
            return ResponseEntity.ok(ApiResponse.success("Site registrado com sucesso! Cliente, projeto e 3 tasks foram criadas (Desenvolvimento, Configurações e Fatura).", client));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao registrar site: " + e.getMessage()));
        }
    }
}