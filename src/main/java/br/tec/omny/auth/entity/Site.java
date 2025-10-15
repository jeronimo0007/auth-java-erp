package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tblsites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "nome_site", nullable = false, length = 255)
    private String nomeSite;

    @Column(name = "dominio", nullable = false, length = 255)
    private String dominio;

    @Column(name = "tipo_site", nullable = false, length = 50)
    private String tipoSite;

    @Column(name = "descricao_negocio", columnDefinition = "TEXT")
    private String descricaoNegocio;

    @Column(name = "publico_alvo", columnDefinition = "TEXT")
    private String publicoAlvo;

    @Column(name = "banner_texto", columnDefinition = "TEXT")
    private String bannerTexto;

    @Column(name = "banner_texto_img", length = 500)
    private String bannerTextoImg;

    

    @Column(name = "quem_somos", columnDefinition = "TEXT")
    private String quemSomos;

    @Column(name = "empresa_imagem", length = 500)
    private String empresaImagem;

    @Column(name = "servicos", columnDefinition = "TEXT")
    private String servicos;

    @Column(name = "servicos_imagens", columnDefinition = "TEXT")
    private String servicosImagens;

    @Column(name = "logo_opcao", length = 20)
    private String logoOpcao; // valores esperados: "profissional" ou "IA"

    @Column(name = "email_desejado", length = 255)
    private String emailDesejado;

    @Column(name = "banner_opcao", length = 20)
    private String bannerOpcao;

    

    @Column(name = "banner_ia_descricao", columnDefinition = "TEXT")
    private String bannerIaDescricao;

    

    @Column(name = "banner_profissional_descricao", columnDefinition = "TEXT")
    private String bannerProfissionalDescricao;

    

    @Column(name = "email_empresa", length = 255)
    private String emailEmpresa;

    @Column(name = "telefone_empresa", length = 50)
    private String telefoneEmpresa;

    @Column(name = "endereco_empresa", columnDefinition = "TEXT")
    private String enderecoEmpresa;

    @Column(name = "secao1_titulo", length = 255)
    private String secao1Titulo;

    @Column(name = "secao1_conteudo", columnDefinition = "TEXT")
    private String secao1Conteudo;

    @Column(name = "secao2_titulo", length = 255)
    private String secao2Titulo;

    @Column(name = "secao2_conteudo", columnDefinition = "TEXT")
    private String secao2Conteudo;

    @Column(name = "logo", length = 500)
    private String logo;

    @Column(name = "favicon", length = 500)
    private String favicon;

    @Column(name = "cor_principal", length = 20)
    private String corPrincipal;

    @Column(name = "cor_secundaria", length = 20)
    private String corSecundaria;

    @Column(name = "estilo", length = 100)
    private String estilo;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "facebook", length = 255)
    private String facebook;

    @Column(name = "linkedin", length = 255)
    private String linkedin;

    @Column(name = "youtube", length = 255)
    private String youtube;

    @Column(name = "tiktok", length = 255)
    private String tiktok;

    @Column(name = "instagram", length = 255)
    private String instagram;

    @Column(name = "status", nullable = false)
    private Integer status = 0; // 0 = Pendente, 1 = Em desenvolvimento, 2 = Concluído, 3 = Cancelado

    @Column(name = "preference", length = 50)
    private String preference; // "descricao" ou outros valores

    @Column(name = "description_site", columnDefinition = "TEXT")
    private String descriptionSite;

    @Column(name = "type_site", length = 50)
    private String typeSite;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Construtor específico
    public Site(Integer clientId, String nomeSite, String dominio, String tipoSite) {
        this.clientId = clientId;
        this.nomeSite = nomeSite;
        this.dominio = dominio;
        this.tipoSite = tipoSite;
    }
}
