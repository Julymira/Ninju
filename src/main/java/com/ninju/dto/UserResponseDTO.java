package com.ninju.dto;

public class UserResponseDTO {
    public Long id;
    public String name;
    public String email;
    public String role;

    public UserResponseDTO(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
