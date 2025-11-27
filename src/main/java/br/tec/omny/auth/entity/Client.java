package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblclients")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long userId;
    
    @Column(name = "company", nullable = false)
    private String company;
    
    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "message", length = 255)
    private String message;

    @Column(name = "type", length = 50)
    private String type;
    

    
    @Column(name = "city")
    private String city;
    

    @Column(name = "address")
    private String address;
    
    @Column(name = "default_currency", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean defaultClient = true;
    
    @Column(name = "active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active = true;
    
    @Column(name = "datecreated")
    private LocalDateTime dateCreated;
    
    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
    }
    
    // Construtores
    public Client() {}
    
    public Client(String company, String phoneNumber, String zip, String city, String state, String address) {
        this.company = company;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.address = address;
    }
    
    // Getters e Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
    

    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    

    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Boolean getDefaultClient() {
        return defaultClient;
    }
    
    public void setDefaultClient(Boolean defaultClient) {
        this.defaultClient = defaultClient;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}

