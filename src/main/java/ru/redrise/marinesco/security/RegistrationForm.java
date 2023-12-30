package ru.redrise.marinesco.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.data.RolesRepository;

@Data
public class RegistrationForm {

    @NotNull
    @Size(min=3, max=32, message="Username must be at least 3 characters long. Should not exceed 32 characters.")
    private String username;
    
    @NotNull
    @Size(min=8, max = 32, message="Password must be at least 8 characters long. Should not exceed 32 characters.")
    private String password;

    private String passwordConfirm;

    @NotNull
    @NotEmpty(message = "Display name could not be blank")
    private String displayname;

    public User toUser(PasswordEncoder passwordEncoder, RolesRepository rolesRepo){
        return new User(
            username, 
            passwordEncoder.encode(password), 
            displayname,
            rolesRepo.findByType(UserRole.Type.USER));
    }
    public boolean isPasswordsNotEqual(){
        return ! password.equals(passwordConfirm);
    }

    public boolean auth(HttpServletRequest request) {
        try{
            request.login(username, password);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
