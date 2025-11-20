package com.zabatstore.zabatstore.controller.dto;

import java.util.List;

public class LoginResponse {

    private String token;
    private String nombre;
    private String email;
    private List<String> roles;

    public LoginResponse(String token, String nombre, String email, List<String> roles) {
        this.token = token;
        this.nombre = nombre;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
