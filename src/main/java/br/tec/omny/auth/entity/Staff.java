package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblstaff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staffid")
    private Integer staffId;
    
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    
    @Column(name = "firstname", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "lastname", nullable = false, length = 50)
    private String lastName;
    
    @Column(name = "facebook", columnDefinition = "MEDIUMTEXT")
    private String facebook;
    
    @Column(name = "linkedin", columnDefinition = "MEDIUMTEXT")
    private String linkedin;
    
    @Column(name = "phonenumber", length = 30)
    private String phoneNumber;
    
    @Column(name = "skype", length = 50)
    private String skype;
    
    @Column(name = "password", nullable = false, length = 250)
    private String password;
    
    @Column(name = "datecreated", nullable = false)
    private LocalDateTime dateCreated;
    
    @Column(name = "profile_image", length = 191)
    private String profileImage;
    
    @Column(name = "last_ip", length = 40)
    private String lastIp;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;
    
    @Column(name = "new_pass_key", length = 32)
    private String newPassKey;
    
    @Column(name = "new_pass_key_requested")
    private LocalDateTime newPassKeyRequested;
    
    @Column(name = "admin", columnDefinition = "INT DEFAULT 0")
    private Integer admin = 0;
    
    @Column(name = "role")
    private Integer role;
    
    @Column(name = "active", columnDefinition = "INT DEFAULT 1")
    private Integer active = 1;
    
    @Column(name = "default_language", length = 40)
    private String defaultLanguage;
    
    @Column(name = "direction", length = 3)
    private String direction;
    
    @Column(name = "media_path_slug", length = 191)
    private String mediaPathSlug;
    
    @Column(name = "is_not_staff", columnDefinition = "INT DEFAULT 0")
    private Integer isNotStaff = 0;
    
    @Column(name = "hourly_rate", columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal hourlyRate = BigDecimal.ZERO;
    
    @Column(name = "two_factor_auth_enabled", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean twoFactorAuthEnabled = false;
    
    @Column(name = "two_factor_auth_code", length = 100)
    private String twoFactorAuthCode;
    
    @Column(name = "two_factor_auth_code_requested")
    private LocalDateTime twoFactorAuthCodeRequested;
    
    @Column(name = "email_signature", columnDefinition = "TEXT")
    private String emailSignature;
    
    @Column(name = "google_auth_secret", columnDefinition = "TEXT")
    private String googleAuthSecret;
    
    @Column(name = "perfex_saas_tenant_id", length = 50)
    private String perfexSaasTenantId = "master";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private StaffType type = StaffType.employee;
    
    @Column(name = "warehouse_id")
    private Integer warehouseId;
    
    @Column(name = "franqueado_id")
    private Integer franqueadoId;
    
    @Column(name = "contractid")
    private Integer contractId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "documentType")
    private DocumentType documentType;
    
    @Column(name = "vat", length = 20)
    private String vat;
    
    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;
    
    @Column(name = "inscricao_municipal", length = 20)
    private String inscricaoMunicipal;
    
    @Column(name = "endereco", length = 255)
    private String endereco;
    
    @Column(name = "cidade", length = 100)
    private String cidade;
    
    @Column(name = "estado", length = 2)
    private String estado;
    
    @Column(name = "cep", length = 10)
    private String cep;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa")
    private TipoPessoa tipoPessoa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empresa")
    private TipoEmpresa tipoEmpresa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "segmento")
    private Segmento segmento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "porte_empresa")
    private PorteEmpresa porteEmpresa;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comissao_representante")
    private TipoComissaoRepresentante tipoComissaoRepresentante;
    
    @Column(name = "percentual_base_representante", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal percentualBaseRepresentante;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento_representante")
    private FormaPagamentoRepresentante formaPagamentoRepresentante;
    
    @Column(name = "dia_vencimento_representante")
    private Integer diaVencimentoRepresentante;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sistema_emissao_nf")
    private SistemaEmissaoNf sistemaEmissaoNf;
    
    @Column(name = "contrato_social", length = 255)
    private String contratoSocial;
    
    @Column(name = "cartao_cnpj", length = 255)
    private String cartaoCnpj;
    
    @Column(name = "certidao_negativa", length = 255)
    private String certidaoNegativa;
    
    @Column(name = "contrato_representacao", length = 255)
    private String contratoRepresentacao;
    
    @Column(name = "warehouse", columnDefinition = "LONGTEXT")
    private String warehouse;
    
    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
    }
    
    // Construtor específico
    public Staff(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
    
    // Enums
    public enum StaffType {
        employee, represented, representative, assistant, agent, pdv, franchisees
    }
    
    public enum DocumentType {
        CPF, CNPJ
    }
    
    public enum TipoPessoa {
        PJ, PF
    }
    
    public enum TipoEmpresa {
        INDUSTRIA, COMERCIO, SERVICOS, DISTRIBUIDOR, REPRESENTANTE
    }
    
    public enum Segmento {
        ALIMENTICIO, AUTOMOTIVO, CONSTRUCAO, ELETRONICO, TEXTIL, OUTRO
    }
    
    public enum PorteEmpresa {
        MEI, MICRO, PEQUENA, MEDIA, GRANDE
    }
    
    public enum TipoComissaoRepresentante {
        FIXA, ESCALONADA, MISTA
    }
    
    public enum FormaPagamentoRepresentante {
        FATURAMENTO, RECEBIMENTO
    }
    
    public enum SistemaEmissaoNf {
        NFE, NFSE, OUTRO
    }
    
}
