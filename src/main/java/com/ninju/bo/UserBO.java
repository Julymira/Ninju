package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.UserRequestDTO;
import com.ninju.dto.UserResponseDTO;
import com.ninju.model.User;
import com.ninju.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserBO {

    @Inject
    UserDao userDao;

    @Inject
    AuditLogDao auditLogDao;

    public List<UserResponseDTO> listAll(String executedBy) {
        auditLogDao.save("LISTAR_USUARIOS", executedBy);
        return userDao.findAll().stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id, String executedBy) {
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        auditLogDao.save("BUSCAR_USUARIO: " + id, executedBy);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public UserResponseDTO create(UserRequestDTO dto, String executedBy) {
        validate(dto, true);
        User user = new User();
        user.setName(dto.name);
        user.setEmail(dto.email);
        user.setPassword(PasswordUtil.hash(dto.password));
        user.setRole(dto.role != null ? dto.role : "USER");
        userDao.save(user);
        auditLogDao.save("CRIAR_USUARIO: " + dto.email, executedBy);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public UserResponseDTO update(Long id, UserRequestDTO dto, String executedBy) {
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        if (dto.name != null && !dto.name.isBlank()) user.setName(dto.name);
        if (dto.email != null && !dto.email.isBlank()) user.setEmail(dto.email);
        if (dto.password != null && !dto.password.isBlank()) user.setPassword(PasswordUtil.hash(dto.password));
        if (dto.role != null && !dto.role.isBlank()) user.setRole(dto.role);
        userDao.update(user);
        auditLogDao.save("ATUALIZAR_USUARIO: " + id, executedBy);
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public void delete(Long id, String executedBy) {
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        userDao.delete(id);
        auditLogDao.save("DELETAR_USUARIO: " + id, executedBy);
    }

    private void validate(UserRequestDTO dto, boolean isNew) {
        if (dto.name == null || dto.name.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (dto.email == null || dto.email.isBlank()) throw new IllegalArgumentException("E-mail é obrigatório.");
        if (isNew && (dto.password == null || dto.password.isBlank())) throw new IllegalArgumentException("Senha é obrigatória.");
    }
}
