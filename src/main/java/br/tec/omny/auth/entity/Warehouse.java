package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblwarehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Integer warehouseId;
    
    @Column(name = "warehouse_code", length = 100)
    private String warehouseCode;
    
    @Column(name = "warehouse_name", columnDefinition = "TEXT")
    private String warehouseName;
    
    @Column(name = "warehouse_address", columnDefinition = "TEXT")
    private String warehouseAddress;
    
    @Column(name = "order")
    private Integer order;
    
    @Column(name = "display", columnDefinition = "INT COMMENT 'display 1: display (yes) 0: not displayed (no)'")
    private Integer display;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @Column(name = "perfex_saas_tenant_id", nullable = false, length = 50)
    private String perfexSaasTenantId;
    
    @Column(name = "city", columnDefinition = "TEXT")
    private String city;
    
    @Column(name = "state", columnDefinition = "TEXT")
    private String state;
    
    @Column(name = "zip_code", columnDefinition = "TEXT")
    private String zipCode;
    
    @Column(name = "country", columnDefinition = "TEXT")
    private String country;
    
    @Column(name = "franqueado_id")
    private Integer franqueadoId;
    
    @Column(name = "cnpj", nullable = false, length = 200)
    private String cnpj;
    
    @Column(name = "im", length = 32)
    private String im;
    
    @Column(name = "ie", length = 32)
    private String ie;
    
    @Column(name = "cep", length = 10)
    private String cep;
    
    @Column(name = "endereco", length = 256)
    private String endereco;
    
    @Column(name = "numero", length = 18)
    private String numero;
    
    @Column(name = "bairro", length = 64)
    private String bairro;
    
    @Column(name = "cidade", length = 128)
    private String cidade;
    
    @Column(name = "complemento", columnDefinition = "TEXT")
    private String complemento;
    
    @Column(name = "estado", length = 2)
    private String estado;
    
    @Column(name = "arquivo_nfe", length = 128)
    private String arquivoNfe;
    
    @Column(name = "name_arquivo_nfe", length = 128)
    private String nameArquivoNfe;
    
    @Column(name = "password_nfe", length = 256)
    private String passwordNfe;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WarehouseType type = WarehouseType.filial;
    
    @Column(name = "razao_social", length = 256)
    private String razaoSocial;
    
    @Column(name = "cnae", length = 64)
    private String cnae;
    
    @Column(name = "crt", length = 32)
    private String crt;
    
    @Column(name = "warehouse_number", length = 32)
    private String warehouseNumber;
    
    @Column(name = "telefone", length = 20)
    private String telefone;
    
    @Column(name = "dt_cto_certifcado_a2")
    private LocalDate dtCtoCertificadoA2;
    
    @Column(name = "tpAmb", nullable = false, columnDefinition = "TINYINT(4) DEFAULT 2")
    private Integer tpAmb = 2;
    
    @Column(name = "ccidade", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer ccidade = 0;
    
    @Column(name = "codigoUF", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer codigoUF = 0;
    
    @Column(name = "situacao_tributaria", length = 16)
    private String situacaoTributaria;
    
    @Column(name = "cscid", length = 16)
    private String cscid;
    
    @Column(name = "csc", length = 64)
    private String csc;
    
    @Column(name = "atualizacao")
    private LocalDateTime atualizacao;
    
    @Column(name = "franquia_id", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer franquiaId = 0;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        atualizacao = LocalDateTime.now();
    }
    
    // Construtor específico
    public Warehouse(String warehouseCode, String warehouseName, String cnpj) {
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
        this.cnpj = cnpj;
    }
    
    // Enums
    public enum WarehouseType {
        filial, franquia, distribuidor, importador, ecommerce
    }
    
}
