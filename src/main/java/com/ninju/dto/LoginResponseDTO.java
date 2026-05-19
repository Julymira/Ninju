package com.ninju.dto;

public class LoginResponseDTO {

    public String token;
    public String name;
    public String role;

    public LoginResponseDTO(String token, String name, String role) {
        this.token = token;
        this.name = name;
        this.role = role;
    }
}
