package ru.redrise.marinesco;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/")
public class RootController {

    @GetMapping
    public String getPage(@ModelAttribute("search") String text) {
        // TODO: SEARCH PAGE + CONTROLLER
        log.info(text);
        return "root";
    }
}
