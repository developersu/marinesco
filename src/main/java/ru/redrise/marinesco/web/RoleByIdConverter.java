package ru.redrise.marinesco.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.security.UserRole;

@Component
public class RoleByIdConverter implements Converter<Long, UserRole>{

    private RolesRepository rolesRepository;

    public RoleByIdConverter(RolesRepository rolesRepository){
        this.rolesRepository = rolesRepository;
    }
    
    @Override
    public UserRole convert(Long id) {
        return rolesRepository.findById(id).orElse(null);
    }
    
}
