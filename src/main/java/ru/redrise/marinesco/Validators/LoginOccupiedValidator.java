package ru.redrise.marinesco.Validators;


import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.redrise.marinesco.data.UserRepository;

public class LoginOccupiedValidator implements ConstraintValidator<LoginOccupiedConstraint, String>{
    
    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return userRepo.findByUsername(login) == null;
    }
}
