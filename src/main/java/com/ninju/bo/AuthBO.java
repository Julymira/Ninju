package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.LoginRequestDTO;
import com.ninju.dto.LoginResponseDTO;
import com.ninju.model.User;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class AuthBO {

    @Inject
    UserDao userDao;

    @Inject
    AuditLogDao auditLogDao;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados de login não informados.");
        }
        if (dto.email == null || dto.email.isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }
        if (dto.password == null || dto.password.isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }

        User user = userDao.authenticate(dto.email, dto.password);

        if (user == null) {
            auditLogDao.save("LOGIN_FALHOU", dto.email);
            throw new SecurityException("E-mail ou senha inválidos.");
        }

        String token = Jwt.issuer("ninju")
                .subject(user.getEmail())
                .groups(Set.of(user.getRole()))
                .claim("name", user.getName())
                .claim("userId", user.getId())
                .expiresIn(Duration.ofHours(8))
                .sign();

        auditLogDao.save("LOGIN_SUCESSO", user.getEmail());

        return new LoginResponseDTO(token, user.getName(), user.getRole());
    }
}
