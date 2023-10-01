package ru.redrise.marinesco.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;
import ru.redrise.marinesco.User;

@Data
public class RegistrationForm {
        private String username;
    private String password;
    private String fullname;
    private String displayname;

    public User toUser(PasswordEncoder passwordEncoder){
        return new User(
            username, 
            passwordEncoder.encode(password), 
            displayname);
    }
}
