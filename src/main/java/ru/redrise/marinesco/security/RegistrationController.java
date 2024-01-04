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
import ru.redrise.marinesco.settings.ApplicationSettings;

@Slf4j
@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserRepository userRepo;
    private RolesRepository rolesRepo;
    private PasswordEncoder passwordEncoder;
    private HttpServletRequest request;

    private ApplicationSettings applicationSettings;

    public RegistrationController(UserRepository userRepo,
            RolesRepository rolesRepo,
            PasswordEncoder passwordEncoder,
            HttpServletRequest request,
            ApplicationSettings applicationSettings) {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.passwordEncoder = passwordEncoder;
        this.request = request;
        this.applicationSettings = applicationSettings;
    }

    @ModelAttribute(name = "registrationForm")
    public RegistrationForm form() {
        return new RegistrationForm();
    }

    @GetMapping
    public String registerForm() {
        if (applicationSettings.isRegistrationAllowed())
            return "registration";
        return "registration_forbidden";
    }

    @PostMapping
    public String postMethodName(@Valid RegistrationForm form, Errors errors, Model model) {
        if (!applicationSettings.isRegistrationAllowed())
            return "redirect:/";
        if (form.isPasswordsNotEqual()) {
            model.addAttribute("passwordsMismatch", "Passwords must be the same.");
            return "registration";
        }
        if (userRepo.findByUsername(form.getUsername()) != null){
            model.addAttribute("loginOccupied", "Login already in use. Please choose another one");
            return "registration";
        }
        if (errors.hasErrors()) {
            return "registration";
        }

        User user = userRepo.save(form.toUser(passwordEncoder, rolesRepo));
        log.info("Added user {} {} {}", user.getId(), user.getUsername(), user.getDisplayname());

        if (form.auth(request))
            return "redirect:/";
        return "redirect:/login";
    }
}
