package ru.redrise.marinesco.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.User;
import ru.redrise.marinesco.UserGenerified;
import ru.redrise.marinesco.data.UserRepository;


//TODO
@Slf4j
@Controller
@RequestMapping("/manage_users")
@PreAuthorize("hasRole('ADMIN')")
public class ManageUsersController {

    private UserRepository userRepository;

    public ManageUsersController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @ModelAttribute(name = "userGenerified")
    public UserGenerified taco() {
        return new UserGenerified();
    }

    @ModelAttribute
    public void addTitle(Model model){
        model.addAttribute("header_text", "Manage users");
    }

    @ModelAttribute
    public void addUsers(Model model){
        Iterable<User> users = userRepository.findAll();
        List<UserGenerified> usersGen = new ArrayList<>();
        for (User user : users){
            usersGen.add(new UserGenerified(user)); // TODO: ADD ARRAY INSTEAD OF ONE!
        }
        model.addAttribute("USR", usersGen); // TODO: ADD ARRAY INSTEAD OF ONE!
    }

    @GetMapping
    public String getPage() {
        return "manage_users";
    }

    @PostMapping("/delete")
    public String processDelete(UserGenerified userGenerified){
        log.info(userGenerified.toString());
        
        userRepository.deleteById(userGenerified.getId());

        return "redirect:/manage_users";
    }
}
