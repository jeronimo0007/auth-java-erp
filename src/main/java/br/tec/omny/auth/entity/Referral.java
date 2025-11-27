package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tblaffiliate_m_referrals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_id")
    private Integer referralId;
    
    @Column(name = "affiliate_id", nullable = false)
    private Integer affiliateId;
    
    @Column(name = "client_id", nullable = false)
    private Integer clientId;
    
    @Column(name = "ua", columnDefinition = "TEXT")
    private String ua;
    
    @Column(name = "ip", length = 255)
    private String ip;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

