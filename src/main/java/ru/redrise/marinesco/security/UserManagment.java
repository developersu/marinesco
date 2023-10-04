package ru.redrise.marinesco.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usersmanagment")
public class UserManagment {
    

    @GetMapping
    public String getPage(){
        return "/usersmanagment";
    }
}
