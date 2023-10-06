
package ru.redrise.marinesco.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HandyErrorController implements ErrorController{
  
    @ModelAttribute(name = "code")
    public String addMisc(HttpServletRequest request){
        return request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString();
    }

    @RequestMapping("/error")
    public String handleError(){
        return "error";
    }
}
