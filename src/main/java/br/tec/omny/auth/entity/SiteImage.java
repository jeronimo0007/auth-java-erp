package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblsiteimages")
public class SiteImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "siteimage_id")
    private Integer siteImageId;

    @Column(name = "site_id", nullable = false)
    private Integer siteId;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    @Column(name = "filename", length = 255)
    private String filename;

    @Column(name = "name_imagem", length = 255)
    private String nameImagem;

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

    public Integer getSiteImageId() {
        return siteImageId;
    }

    public void setSiteImageId(Integer siteImageId) {
        this.siteImageId = siteImageId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNameImagem() {
        return nameImagem;
    }

    public void setNameImagem(String nameImagem) {
        this.nameImagem = nameImagem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}