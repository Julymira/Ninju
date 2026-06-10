package com.ninju.dto;

public class LoginResponseDTO {

    public String token;
    public Long id;
    public String name;
    public String email;
    public String role;
    public String avatar;
    public Double weight;
    public Integer calorieMeta;
    public Integer carbsMetaPct;
    public Integer proteinMetaPct;
    public Integer fatMetaPct;

    public LoginResponseDTO(String token, Long id, String name, String email, String role,
                            String avatar, Double weight,
                            Integer calorieMeta, Integer carbsMetaPct,
                            Integer proteinMetaPct, Integer fatMetaPct) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
        this.weight = weight;
        this.calorieMeta = calorieMeta;
        this.carbsMetaPct = carbsMetaPct;
        this.proteinMetaPct = proteinMetaPct;
        this.fatMetaPct = fatMetaPct;
    }
}
