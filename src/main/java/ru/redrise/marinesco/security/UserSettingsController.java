package ru.redrise.marinesco.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.UserSettingsForm;
import ru.redrise.marinesco.data.UserRepository;

@Slf4j
@Controller
@RequestMapping("/profile")
public class UserSettingsController {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserSettingsController(UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute
    public void addMisc(Model model, @AuthenticationPrincipal User user){
        final String displayName = user.getDisplayname();
        model.addAttribute("header_text", "Welcome " + displayName);

        UserSettingsForm form = new UserSettingsForm();
        form.setDisplayname(displayName);
        model.addAttribute( "userSettingsForm", form);
    }

    @ModelAttribute
    public UserSettingsForm addSettingsForm(){
        return new UserSettingsForm();
    }

    @GetMapping
    public String getPage(){
        return "redirect:/profile/settings";
    }

    @GetMapping("/settings")
    public String getSettingsFirstPage(){
        return "user_settings";
    }

    @PostMapping("/settings")
    public String getSettingsPage(@Valid UserSettingsForm userSettingsForm, 
            Errors errors, 
            @AuthenticationPrincipal User user,
            Model model){
        if (errors.hasErrors())     
            return "user_settings";

        user.setDisplayname(userSettingsForm.getDisplayname());

        if (userSettingsForm.isNewPasswordSet())
            user.setPassword(passwordEncoder.encode(userSettingsForm.getNewPassword()));

        userRepo.save(user);
        
        return "redirect:/profile/settings";
    }
}
