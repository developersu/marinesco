package ru.redrise.marinesco.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.data.RolesRepository;

@Data
public class RegistrationForm {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String fullname;
    @NotNull
    private String displayname;

    public User toUser(PasswordEncoder passwordEncoder, RolesRepository rolesRepo){
        return new User(
            username, 
            passwordEncoder.encode(password), 
            displayname,
            rolesRepo.findByType(UserRole.Type.USER));
    }
}
