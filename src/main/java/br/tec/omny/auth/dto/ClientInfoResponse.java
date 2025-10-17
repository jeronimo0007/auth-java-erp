package br.tec.omny.auth.dto;

public class ClientInfoResponse {
    
    private String company;
    private String email;
    private String phoneNumber;
    
    public ClientInfoResponse() {}
    
    public ClientInfoResponse(String company, String email, String phoneNumber) {
        this.company = company;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters e Setters
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
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
}
