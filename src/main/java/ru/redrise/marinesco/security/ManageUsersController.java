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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.UserGenerified;
import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.data.UserRepository;

@Slf4j
@Controller
@RequestMapping("/settings/manage_users")
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
    public UserGenerified createUserGenerified() {
        return new UserGenerified();
    }

    @ModelAttribute
    public void addTitle(Model model) {
        model.addAttribute("header_text", "Manage users");
        model.addAttribute("administratorAddUserForm", new AdministratorAddUserForm());
    }

    @ModelAttribute
    public void addUsers(Model model) {
        Iterable<User> users = userRepository.findAll();
        List<UserGenerified> usersGen = new ArrayList<>();
        List<UserRole> allRolesList = new ArrayList<>();
        rolesRepository.findAll().forEach(allRolesList::add);

        for (User user : users) {
            usersGen.add(new UserGenerified(user, allRolesList));
        }
        model.addAttribute("users", usersGen);
    }

    @ModelAttribute
    public void addRoles(Model model) {
        Iterable<UserRole> roles = rolesRepository.findAll();
        model.addAttribute("rolesSet", roles);
    }

    @GetMapping
    public String getPage() {
        return "manage_users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        try {
            long userId = Long.parseLong(id);
            userRepository.deleteById(userId);
        } catch (Exception e) {
            log.error(id, e);
        }

        return "redirect:/settings/manage_users";
    }

    @PostMapping("/update")
    public String update(UserGenerified userGenerified) {
        User user = userRepository.findById(userGenerified.getId()).get();
        if (user == null)
            return "redirect:/settings/manage_users";

        user.setAuthorities(userGenerified.getAuthorities());
        user.setDisplayname(userGenerified.getDisplayName());
        String password = userGenerified.getPassword().trim();
        if (! password.trim().isEmpty())
            user.setPassword(passwordEncoder.encode(userGenerified.getPassword()));
        userRepository.save(user);
        
        return "redirect:/settings/manage_users";
    }

    @PostMapping
    public String processNewUser(@Valid AdministratorAddUserForm form, Errors errors, Model model) {
        if (userRepository.findByUsername(form.getUsername()) != null) {
            model.addAttribute("loginOccupied", "Login already in use. Please choose another one");
            return "manage_users";
        }
        if (errors.hasErrors()) {
            log.info(errors.getAllErrors().toString());
            return "manage_users";
        }

        User user = userRepository.save(form.toUser(passwordEncoder));
        log.info("Added user {} {} {} {}", user.getId(), user.getUsername(), user.getDisplayname(),
                user.getAuthorities().get(0));
        // Reloads page therefore new records appears
        return "redirect:/settings/manage_users";
    }
}
