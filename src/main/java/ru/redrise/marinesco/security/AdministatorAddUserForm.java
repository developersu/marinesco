package ru.redrise.marinesco.security;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.redrise.marinesco.User;

//TODO: refactor along with RegistrationForm.java
@Data
public class AdministatorAddUserForm {

    @NotNull
    @Size(min=3, max=32, message="Username must be at least 3 characters long. Should not exceed 32 characters.")
    private String username;

    @NotNull
    @Size(min=8, max = 32, message="Password must be at least 8 characters long. Should not exceed 32 characters.")
    private String password;

    @NotNull
    @NotEmpty(message = "Display name could not be blank")
    private String displayname;

    @NotNull
    @Size(min=1, message="You must choose at least 1 role")
    private List<UserRole> athorities;

    public User toUser(PasswordEncoder passwordEncoder){
        return new User(
            username, 
            passwordEncoder.encode(password), 
            displayname,
            athorities);
    }
}
