package ru.redrise.marinesco.Validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = LoginOccupiedValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginOccupiedConstraint {
    String message() default "Login already taken. Please use another one";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
