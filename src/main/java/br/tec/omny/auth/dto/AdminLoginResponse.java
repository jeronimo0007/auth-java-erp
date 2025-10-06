package br.tec.omny.auth.dto;

import br.tec.omny.auth.entity.Staff;
import br.tec.omny.auth.entity.Warehouse;

public class AdminLoginResponse {
    
    private boolean success;
    private String message;
    private Staff user;
    private String token;
    private Warehouse warehouse;
    
    // Construtores
    public AdminLoginResponse() {}
    
    public AdminLoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public AdminLoginResponse(boolean success, String message, Staff user, String token, Warehouse warehouse) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.token = token;
        this.warehouse = warehouse;
    }
    
    // Métodos estáticos para facilitar criação de respostas
    public static AdminLoginResponse success(String message, Staff user, String token, Warehouse warehouse) {
        return new AdminLoginResponse(true, message, user, token, warehouse);
    }
    
    public static AdminLoginResponse error(String message) {
        return new AdminLoginResponse(false, message);
    }
    
    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Staff getUser() {
        return user;
    }
    
    public void setUser(Staff user) {
        this.user = user;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Warehouse getWarehouse() {
        return warehouse;
    }
    
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
