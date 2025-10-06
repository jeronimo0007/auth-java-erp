package br.tec.omny.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class SiteRegisterRequest {
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @NotBlank(message = "Celular é obrigatório")
    private String phonenumber;
    
    @NotBlank(message = "Empresa é obrigatória")
    private String company;
    
    @NotBlank(message = "Nome do site é obrigatório")
    private String nomeSite;
    
    @NotBlank(message = "Domínio é obrigatório")
    private String dominio;
    
    @NotBlank(message = "Descrição do negócio é obrigatória")
    private String descricaoNegocio;
    
    @NotBlank(message = "Público alvo é obrigatório")
    private String publicoAlvo;
    
    @NotBlank(message = "Texto do banner é obrigatório")
    private String bannerTexto;
    
    private String bannerSecundario;
    
    private String bannerTerciario;

    // Imagens dos banners (opcionais)
    private org.springframework.web.multipart.MultipartFile bannerTextoImg;
    private org.springframework.web.multipart.MultipartFile bannerSecundarioImg;
    private org.springframework.web.multipart.MultipartFile bannerTerciarioImg;

    // Favicon opcional
    private org.springframework.web.multipart.MultipartFile favicon;
    
    @NotBlank(message = "Tipo do site é obrigatório")
    private String tipoSite;
    
    @NotBlank(message = "Quem somos é obrigatório")
    private String quemSomos;
    
    private MultipartFile empresaImagem;
    
    @NotBlank(message = "Serviços são obrigatórios")
    private String servicos;
    
    private List<MultipartFile> servicosImagens;

    @NotBlank(message = "Opção de logo é obrigatória (profissional ou IA)")
    private String logoOpcao;
    
    @NotBlank(message = "Email da empresa é obrigatório")
    @Email(message = "Email da empresa deve ter um formato válido")
    private String emailEmpresa;
    
    @NotBlank(message = "Telefone da empresa é obrigatório")
    private String telefoneEmpresa;
    
    @NotBlank(message = "Endereço da empresa é obrigatório")
    private String enderecoEmpresa;
    
    @NotBlank(message = "Título da seção 1 é obrigatório")
    private String secao1Titulo;
    
    @NotBlank(message = "Conteúdo da seção 1 é obrigatório")
    private String secao1Conteudo;
    
    @NotBlank(message = "Título da seção 2 é obrigatório")
    private String secao2Titulo;
    
    @NotBlank(message = "Conteúdo da seção 2 é obrigatório")
    private String secao2Conteudo;
    
    private MultipartFile logo;
    
    @NotBlank(message = "Cor principal é obrigatória")
    private String corPrincipal;
    
    @NotBlank(message = "Cor secundária é obrigatória")
    private String corSecundaria;
    
    @NotBlank(message = "Estilo é obrigatório")
    private String estilo;
    
    private String observacoes;
    
    @NotBlank(message = "Primeiro nome é obrigatório")
    private String firstname;
    
    @NotBlank(message = "Último nome é obrigatório")
    private String lastname;
    
    private String facebook;
    
    private String linkedin;
    
    private String youtube;
    
    private String tiktok;
    
    private String instagram;
    
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

    public org.springframework.web.multipart.MultipartFile getBannerSecundarioImg() {
        return bannerSecundarioImg;
    }

    public void setBannerSecundarioImg(org.springframework.web.multipart.MultipartFile bannerSecundarioImg) {
        this.bannerSecundarioImg = bannerSecundarioImg;
    }

    public org.springframework.web.multipart.MultipartFile getBannerTerciarioImg() {
        return bannerTerciarioImg;
    }

    public void setBannerTerciarioImg(org.springframework.web.multipart.MultipartFile bannerTerciarioImg) {
        this.bannerTerciarioImg = bannerTerciarioImg;
    }

    public org.springframework.web.multipart.MultipartFile getFavicon() {
        return favicon;
    }

    public void setFavicon(org.springframework.web.multipart.MultipartFile favicon) {
        this.favicon = favicon;
    }
    public String getBannerSecundario() {
        return bannerSecundario;
    }
    
    public void setBannerSecundario(String bannerSecundario) {
        this.bannerSecundario = bannerSecundario;
    }
    
    public String getBannerTerciario() {
        return bannerTerciario;
    }
    
    public void setBannerTerciario(String bannerTerciario) {
        this.bannerTerciario = bannerTerciario;
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
}
