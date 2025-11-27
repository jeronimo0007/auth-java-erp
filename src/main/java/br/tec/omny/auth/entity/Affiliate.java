package br.tec.omny.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblaffiliate_m_affiliates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Affiliate {
    
    @Id
    @Column(name = "affiliate_id")
    private Integer affiliateId;
    
    @Column(name = "affiliate_slug", length = 255)
    private String affiliateSlug;
}

