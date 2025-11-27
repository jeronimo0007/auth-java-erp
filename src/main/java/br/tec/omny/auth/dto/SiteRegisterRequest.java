package br.tec.omny.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import br.tec.omny.auth.validation.ValidationGroups;

import java.util.List;

public class SiteRegisterRequest {
    
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @JsonAlias({"phoneNumber", "phone_number"})
    private String phonenumber;
    
    private String company;
    
    @JsonAlias("nome_site")
    private String nomeSite;
    
    private String dominio;
    
    @JsonAlias("descricao_negocio")
    private String descricaoNegocio;
    
    @JsonAlias("publico_alvo")
    private String publicoAlvo;
    
    @JsonAlias("banner_texto")
    private String bannerTexto;
    
    

    // Imagens dos banners (opcionais)
    private org.springframework.web.multipart.MultipartFile bannerTextoImg;
    

    // Favicon opcional
    private org.springframework.web.multipart.MultipartFile favicon;
    
    @JsonAlias("tipo_site")
    private String tipoSite;
    
    private String quemSomos;
    
    private MultipartFile empresaImagem;
    
    private String servicos;
    
    private List<MultipartFile> servicosImagens;

    private String logoOpcao;
    
    @Email(message = "Email desejado deve ter um formato válido")
    @JsonAlias("email_desejado")
    private String emailDesejado;
    
    @JsonAlias("banner_opcao")
    private String bannerOpcao;
    
    
    
    @JsonAlias("banner_ia_descricao")
    private String bannerIaDescricao;
    
    
    
    @JsonAlias("banner_profissional_descricao")
    private String bannerProfissionalDescricao;
    
    
    
    @Email(message = "Email da empresa deve ter um formato válido")
    @JsonAlias("email_empresa")
    private String emailEmpresa;
    
    @JsonAlias("telefone_empresa")
    private String telefoneEmpresa;
    
    @JsonAlias("endereco_empresa")
    private String enderecoEmpresa;
    
    @JsonAlias("secao1_titulo")
    private String secao1Titulo;
    
    @JsonAlias("secao1_conteudo")
    private String secao1Conteudo;
    
    @JsonAlias("secao2_titulo")
    private String secao2Titulo;
    
    @JsonAlias("secao2_conteudo")
    private String secao2Conteudo;
    
    private MultipartFile logo;
    
    @JsonAlias("cor_principal")
    private String corPrincipal;
    
    @JsonAlias("cor_secundaria")
    private String corSecundaria;
    
    private String estilo;
    
    private String observacoes;
    
    @JsonAlias("firstName")
    private String firstname;
    
    @JsonAlias("lastName")
    private String lastname;
    
    private String facebook;
    
    private String linkedin;
    
    private String youtube;
    
    private String tiktok;
    
    private String instagram;
    
    // Novos campos
    private String preference;
    
    @NotBlank(message = "Descrição do site é obrigatória quando preference é 'descricao'", groups = {ValidationGroups.DescriptionRequired.class})
    @JsonAlias("description_site")
    private String descriptionSite;
    
    @JsonAlias("tipo_site")
    private String typeSite;
    private Long userId; // Para validar se cliente já existe
    
    private String password; // Senha do usuário
    
    // reCAPTCHA
    private String recaptchaToken;

    private String message;
    private String type;

    @JsonAlias("product_id")
    private String productId;
    private String afm;
    private String userAgent;
    private String clientIp;
    
    // Flag para controlar envio para fila RabbitMQ
    private Boolean ia;
    
    // Construtores
    public SiteRegisterRequest() {}
    
    // Getters e Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhonenumber() {
        return phonenumber;
    }
    
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getNomeSite() {
        return nomeSite;
    }
    
    public void setNomeSite(String nomeSite) {
        this.nomeSite = nomeSite;
    }
    
    public String getDominio() {
        return dominio;
    }
    
    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
    
    public String getDescricaoNegocio() {
        return descricaoNegocio;
    }
    
    public void setDescricaoNegocio(String descricaoNegocio) {
        this.descricaoNegocio = descricaoNegocio;
    }
    
    public String getPublicoAlvo() {
        return publicoAlvo;
    }
    
    public void setPublicoAlvo(String publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }
    
    public String getBannerTexto() {
        return bannerTexto;
    }
    
    public void setBannerTexto(String bannerTexto) {
        this.bannerTexto = bannerTexto;
    }
    
    public org.springframework.web.multipart.MultipartFile getBannerTextoImg() {
        return bannerTextoImg;
    }

    public void setBannerTextoImg(org.springframework.web.multipart.MultipartFile bannerTextoImg) {
        this.bannerTextoImg = bannerTextoImg;
    }

    

    public org.springframework.web.multipart.MultipartFile getFavicon() {
        return favicon;
    }

    public void setFavicon(org.springframework.web.multipart.MultipartFile favicon) {
        this.favicon = favicon;
    }
    
    
    public String getTipoSite() {
        return tipoSite;
    }
    
    public void setTipoSite(String tipoSite) {
        this.tipoSite = tipoSite;
    }
    
    public String getQuemSomos() {
        return quemSomos;
    }
    
    public void setQuemSomos(String quemSomos) {
        this.quemSomos = quemSomos;
    }
    
    public MultipartFile getEmpresaImagem() {
        return empresaImagem;
    }
    
    public void setEmpresaImagem(MultipartFile empresaImagem) {
        this.empresaImagem = empresaImagem;
    }
    
    public String getServicos() {
        return servicos;
    }
    
    public void setServicos(String servicos) {
        this.servicos = servicos;
    }
    
    public List<MultipartFile> getServicosImagens() {
        return servicosImagens;
    }
    
    public void setServicosImagens(List<MultipartFile> servicosImagens) {
        this.servicosImagens = servicosImagens;
    }
    
    public String getLogoOpcao() {
        return logoOpcao;
    }

    public void setLogoOpcao(String logoOpcao) {
        this.logoOpcao = logoOpcao;
    }
    
    public String getEmailDesejado() {
        return emailDesejado;
    }
    
    public void setEmailDesejado(String emailDesejado) {
        this.emailDesejado = emailDesejado;
    }
    
    public String getBannerOpcao() {
        return bannerOpcao;
    }
    
    public void setBannerOpcao(String bannerOpcao) {
        this.bannerOpcao = bannerOpcao;
    }
    
    
    
    public String getBannerIaDescricao() {
        return bannerIaDescricao;
    }
    
    public void setBannerIaDescricao(String bannerIaDescricao) {
        this.bannerIaDescricao = bannerIaDescricao;
    }
    
    
    
    public String getBannerProfissionalDescricao() {
        return bannerProfissionalDescricao;
    }
    
    public void setBannerProfissionalDescricao(String bannerProfissionalDescricao) {
        this.bannerProfissionalDescricao = bannerProfissionalDescricao;
    }
    
    
    
    public String getEmailEmpresa() {
        return emailEmpresa;
    }
    
    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }
    
    public String getTelefoneEmpresa() {
        return telefoneEmpresa;
    }
    
    public void setTelefoneEmpresa(String telefoneEmpresa) {
        this.telefoneEmpresa = telefoneEmpresa;
    }
    
    public String getEnderecoEmpresa() {
        return enderecoEmpresa;
    }
    
    public void setEnderecoEmpresa(String enderecoEmpresa) {
        this.enderecoEmpresa = enderecoEmpresa;
    }
    
    public String getSecao1Titulo() {
        return secao1Titulo;
    }
    
    public void setSecao1Titulo(String secao1Titulo) {
        this.secao1Titulo = secao1Titulo;
    }
    
    public String getSecao1Conteudo() {
        return secao1Conteudo;
    }
    
    public void setSecao1Conteudo(String secao1Conteudo) {
        this.secao1Conteudo = secao1Conteudo;
    }
    
    public String getSecao2Titulo() {
        return secao2Titulo;
    }
    
    public void setSecao2Titulo(String secao2Titulo) {
        this.secao2Titulo = secao2Titulo;
    }
    
    public String getSecao2Conteudo() {
        return secao2Conteudo;
    }
    
    public void setSecao2Conteudo(String secao2Conteudo) {
        this.secao2Conteudo = secao2Conteudo;
    }
    
    public MultipartFile getLogo() {
        return logo;
    }
    
    public void setLogo(MultipartFile logo) {
        this.logo = logo;
    }
    
    public String getCorPrincipal() {
        return corPrincipal;
    }
    
    public void setCorPrincipal(String corPrincipal) {
        this.corPrincipal = corPrincipal;
    }
    
    public String getCorSecundaria() {
        return corSecundaria;
    }
    
    public void setCorSecundaria(String corSecundaria) {
        this.corSecundaria = corSecundaria;
    }
    
    public String getEstilo() {
        return estilo;
    }
    
    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getFacebook() {
        return facebook;
    }
    
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
    
    public String getLinkedin() {
        return linkedin;
    }
    
    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
    
    public String getYoutube() {
        return youtube;
    }
    
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
    
    public String getTiktok() {
        return tiktok;
    }
    
    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }
    
    public String getInstagram() {
        return instagram;
    }
    
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
    
    public String getPreference() {
        return preference;
    }
    
    public void setPreference(String preference) {
        this.preference = preference;
    }
    
    public String getDescriptionSite() {
        return descriptionSite;
    }
    
    public void setDescriptionSite(String descriptionSite) {
        this.descriptionSite = descriptionSite;
    }
    
    public String getTypeSite() {
        return typeSite;
    }
    
    public void setTypeSite(String typeSite) {
        this.typeSite = typeSite;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // reCAPTCHA
    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public Boolean getIa() {
        return ia;
    }
    
    public void setIa(Boolean ia) {
        this.ia = ia;
    }
    
}
