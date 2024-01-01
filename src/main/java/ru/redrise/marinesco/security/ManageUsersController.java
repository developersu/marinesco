package ru.redrise.marinesco.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.AdministatorAddUserForm;
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.UserGenerified;
import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.data.UserRepository;

//TODO
@Slf4j
@Controller
@RequestMapping("/manage_users")
@PreAuthorize("hasRole('ADMIN')")
public class ManageUsersController {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    public ManageUsersController(UserRepository userRepository,
            RolesRepository rolesRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute(name = "userGenerified")
    public UserGenerified taco() {
        return new UserGenerified();
    }

    @ModelAttribute
    public void addTitle(Model model) {
        model.addAttribute("header_text", "Manage users");
        model.addAttribute("administatorAddUserForm", new AdministatorAddUserForm());
    }

    @ModelAttribute
    public void addUsers(Model model) {
        Iterable<User> users = userRepository.findAll();
        List<UserGenerified> usersGen = new ArrayList<>();
        for (User user : users) {
            usersGen.add(new UserGenerified(user));
        }
        model.addAttribute("USR", usersGen);
    }
    @ModelAttribute
    public void addRoles(Model model) {
        Iterable<UserRole> roles = rolesRepository.findAll();
        model.addAttribute("roles", roles);
    }

    @GetMapping
    public String getPage() {
        return "manage_users";
    }

    @PostMapping("/delete")
    public String processDelete(UserGenerified userGenerified) {
        log.info(userGenerified.toString());
        
        userRepository.deleteById(userGenerified.getId());

        return "redirect:/manage_users";
    }

    @PostMapping
    public String processNewUser(@Valid AdministatorAddUserForm form, Errors errors, Model model) {
        if (userRepository.findByUsername(form.getUsername()) != null) {
            model.addAttribute("loginOccupied", "Login already in use. Please choose another one");
            return "manage_users";
        }
        if (errors.hasErrors()) {
            log.info(errors.getAllErrors().toString());
            return "manage_users";
        }

        User user = userRepository.save(form.toUser(passwordEncoder));
        log.info("Added user {} {} {}", user.getId(), user.getUsername(), user.getDisplayname(), user.getAuthorities().get(0));
        // Reloads page therefore new records appears
        return "redirect:/manage_users";
    }
}
