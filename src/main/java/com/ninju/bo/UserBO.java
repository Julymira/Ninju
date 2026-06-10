package com.ninju.bo;

import com.ninju.dao.AuditLogDao;
import com.ninju.dao.UserDao;
import com.ninju.dto.ChangePasswordDTO;
import com.ninju.dto.UserGoalsDTO;
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
        return userDao.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id, String executedBy) {
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        auditLogDao.save("BUSCAR_USUARIO: " + id, executedBy);
        return toDTO(user);
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
        return toDTO(user);
    }

    public UserResponseDTO update(Long id, UserRequestDTO dto, String executedBy) {
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        if (dto.name != null && !dto.name.isBlank()) user.setName(dto.name);
        if (dto.email != null && !dto.email.isBlank()) user.setEmail(dto.email);
        if (dto.password != null && !dto.password.isBlank()) user.setPassword(PasswordUtil.hash(dto.password));
        if (dto.role != null && !dto.role.isBlank()) user.setRole(dto.role);
        if (dto.avatar != null) user.setAvatar(dto.avatar.isBlank() ? null : dto.avatar);
        userDao.update(user);
        auditLogDao.save("ATUALIZAR_USUARIO: " + id, executedBy);
        return toDTO(user);
    }

    public void changePassword(Long id, ChangePasswordDTO dto, String executedBy) {
        if (dto.currentPassword == null || dto.currentPassword.isBlank())
            throw new IllegalArgumentException("Senha atual é obrigatória.");
        if (dto.newPassword == null || dto.newPassword.length() < 6)
            throw new IllegalArgumentException("A nova senha deve ter ao menos 6 caracteres.");

        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        if (!PasswordUtil.verify(dto.currentPassword, user.getPassword()))
            throw new SecurityException("Senha atual incorreta.");

        user.setPassword(PasswordUtil.hash(dto.newPassword));
        userDao.update(user);
        auditLogDao.save("ALTERAR_SENHA: " + id, executedBy);
    }

    public UserResponseDTO updateGoals(Long id, UserGoalsDTO dto, String executedBy) {
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        if (dto.weight != null) user.setWeight(dto.weight);
        if (dto.calorieMeta != null) user.setCalorieMeta(dto.calorieMeta);
        if (dto.carbsMetaPct != null) user.setCarbsMetaPct(dto.carbsMetaPct);
        if (dto.proteinMetaPct != null) user.setProteinMetaPct(dto.proteinMetaPct);
        if (dto.fatMetaPct != null) user.setFatMetaPct(dto.fatMetaPct);
        userDao.update(user);
        auditLogDao.save("ATUALIZAR_METAS: " + id, executedBy);
        return toDTO(user);
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

    private UserResponseDTO toDTO(User u) {
        return new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(),
                u.getAvatar(), u.getWeight(),
                u.getCalorieMeta(), u.getCarbsMetaPct(),
                u.getProteinMetaPct(), u.getFatMetaPct());
    }
}
