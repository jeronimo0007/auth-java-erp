package br.tec.omny.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    
    // Primeira linha: Nome e Telefone
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
    // Segunda linha: Email e Senha
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String password;
    
    // Outros campos
    @NotBlank(message = "Nome da empresa é obrigatório")
    private String company;
    private String zip;
    private String city;
    private String state;
    private String address;
    
    // Construtores
    public RegisterRequest() {}
    
    public RegisterRequest(String firstName, String lastName, String phoneNumber, String email, String password, 
                          String company, String zip, String city, String state, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.company = company;
        this.zip = zip;
        this.city = city;
        this.state = state;
        this.address = address;
    }
    
    // Getters e Setters
    // Primeira linha: Nome e Telefone
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
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    // Segunda linha: Email e Senha
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Outros campos
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getZip() {
        return zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}

