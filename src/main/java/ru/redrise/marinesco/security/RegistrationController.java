package ru.redrise.marinesco.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.data.UserRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserRepository userRepo;
    private RolesRepository rolesRepo;
    private PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepo, RolesRepository rolesRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute(name = "registrationForm")
    public RegistrationForm form() {
        return new RegistrationForm();
    }

    @GetMapping
    public String registerForm() {
        return "registration";
    }

    @PostMapping
    public String postMethodName(@Valid RegistrationForm registerForm, Errors errors, Model model) {
        if (registerForm.isPasswordsNotEqual()){
            model.addAttribute("passwordsMismatch", "Passwords must be the same.");
            return "registration";
        }
        if (errors.hasErrors()) {
            return "registration";
        }

        userRepo.save(registerForm.toUser(passwordEncoder, rolesRepo));
        return "redirect:/login";
    }
}
