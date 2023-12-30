package ru.redrise.marinesco.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.data.UserRepository;

@Slf4j
@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserRepository userRepo;
    private RolesRepository rolesRepo;
    private PasswordEncoder passwordEncoder;
    private HttpServletRequest request;

    public RegistrationController(UserRepository userRepo,
            RolesRepository rolesRepo,
            PasswordEncoder passwordEncoder,
            HttpServletRequest request) {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.passwordEncoder = passwordEncoder;
        this.request = request;
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
        if (registerForm.isPasswordsNotEqual()) {
            model.addAttribute("passwordsMismatch", "Passwords must be the same.");
            return "registration";
        }
        if (errors.hasErrors()) {
            return "registration";
        }

        User user = userRepo.save(registerForm.toUser(passwordEncoder, rolesRepo));
        log.info("Added user {} {} {}", user.getId(), user.getUsername(), user.getDisplayname());

        if (registerForm.auth(request))
            return "redirect:/";
        return "redirect:/login";
    }
}
