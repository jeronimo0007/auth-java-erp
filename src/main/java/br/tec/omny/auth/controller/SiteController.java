package br.tec.omny.auth.controller;

import br.tec.omny.auth.dto.ApiResponse;
import br.tec.omny.auth.dto.SiteRegisterRequest;
import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam("email") String email,
            @RequestParam("phonenumber") String phonenumber,
            @RequestParam("company") String company,
            @RequestParam("nome_site") String nomeSite,
            @RequestParam("dominio") String dominio,
            @RequestParam("descricao_negocio") String descricaoNegocio,
            @RequestParam("publico_alvo") String publicoAlvo,
            @RequestParam("banner_texto") String bannerTexto,
            @RequestParam(value = "banner_texto_img", required = false) MultipartFile bannerTextoImg,
            
            @RequestParam("tipo_site") String tipoSite,
            @RequestParam("quem_somos") String quemSomos,
            @RequestParam(value = "empresa_imagem", required = false) MultipartFile empresaImagem,
            @RequestParam("servicos") String servicos,
            @RequestParam(value = "servicos_imagens[]", required = false) MultipartFile[] servicosImagens,
            @RequestParam("logo_opcao") String logoOpcao,
            @RequestParam("email_desejado") String emailDesejado,
            @RequestParam("banner_opcao") String bannerOpcao,
            @RequestParam(value = "banner_ia_descricao", required = false) String bannerIaDescricao,
            
            @RequestParam(value = "banner_profissional_descricao", required = false) String bannerProfissionalDescricao,
            @RequestParam("email_empresa") String emailEmpresa,
            @RequestParam("telefone_empresa") String telefoneEmpresa,
            @RequestParam("endereco_empresa") String enderecoEmpresa,
            @RequestParam("secao1_titulo") String secao1Titulo,
            @RequestParam("secao1_conteudo") String secao1Conteudo,
            @RequestParam("secao2_titulo") String secao2Titulo,
            @RequestParam("secao2_conteudo") String secao2Conteudo,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "favicon", required = false) MultipartFile favicon,
            @RequestParam("cor_principal") String corPrincipal,
            @RequestParam("cor_secundaria") String corSecundaria,
            @RequestParam("estilo") String estilo,
            @RequestParam(value = "observacoes", required = false) String observacoes,
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam(value = "facebook", required = false) String facebook,
            @RequestParam(value = "linkedin", required = false) String linkedin,
            @RequestParam(value = "youtube", required = false) String youtube,
            @RequestParam(value = "tiktok", required = false) String tiktok,
            @RequestParam(value = "instagram", required = false) String instagram) {
        
        try {
            // Cria o objeto de request
            SiteRegisterRequest request = new SiteRegisterRequest();
            request.setEmail(email);
            request.setPhonenumber(phonenumber);
            request.setCompany(company);
            request.setNomeSite(nomeSite);
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
            request.setFirstname(firstname);
            request.setLastname(lastname);
            request.setFacebook(facebook);
            request.setLinkedin(linkedin);
            request.setYoutube(youtube);
            request.setTiktok(tiktok);
            request.setInstagram(instagram);
            
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