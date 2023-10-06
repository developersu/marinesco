package ru.redrise.marinesco;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@PreAuthorize("hasRole('USER')")
@Controller
public class RootController {

    @GetMapping("/")
    public String home(){
        return "root";
    }
}
