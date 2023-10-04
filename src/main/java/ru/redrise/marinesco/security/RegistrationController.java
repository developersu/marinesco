package ru.redrise.marinesco.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.data.UserRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserRepository userRepo;
    private RolesRepository rolesRepo;
    private PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepo, RolesRepository rolesRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerForm(){
        return "registration";
    }

    @PostMapping
    public String postMethodName(RegistrationForm registrationForm) {
        userRepo.save(registrationForm.toUser(passwordEncoder, rolesRepo));
        return "redirect:/login";
    }
}
