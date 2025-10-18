package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblcontacts")
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "userid", nullable = false)
    private Long userId;
    
    @Column(name = "is_primary", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isPrimary = false;
    
    @Column(name = "firstname")
    private String firstName;
    
    @Column(name = "lastname")
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phonenumber")
    private String phoneNumber;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "datecreated")
    private LocalDateTime dateCreated;
    
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;
    
    @Column(name = "email_verification_key")
    private String emailVerificationKey;
    
    @Column(name = "email_verification_sent_at")
    private LocalDateTime emailVerificationSentAt;
    
    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        // Configura campos de verificação de email para novos cadastros
        emailVerifiedAt = LocalDateTime.now(); // Email já verificado no cadastro
        emailVerificationKey = null; // Sem chave de verificação
        emailVerificationSentAt = null; // Não foi enviado email de verificação
    }
    
    // Construtores
    public Contact() {}
    
    public Contact(Long userId, String firstName, String lastName, String email, String phoneNumber, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }
    
    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }
    
    public String getEmailVerificationKey() {
        return emailVerificationKey;
    }
    
    public void setEmailVerificationKey(String emailVerificationKey) {
        this.emailVerificationKey = emailVerificationKey;
    }
    
    public LocalDateTime getEmailVerificationSentAt() {
        return emailVerificationSentAt;
    }
    
    public void setEmailVerificationSentAt(LocalDateTime emailVerificationSentAt) {
        this.emailVerificationSentAt = emailVerificationSentAt;
    }
}

