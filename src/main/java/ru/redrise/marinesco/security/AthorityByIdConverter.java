package ru.redrise.marinesco.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ru.redrise.marinesco.data.RolesRepository;

@Component
public class AthorityByIdConverter implements Converter<Long, UserRole>{

    private final RolesRepository rolesRepo;

    public AthorityByIdConverter(RolesRepository rolesRepo){
        this.rolesRepo = rolesRepo;
    }

    @Override
    public UserRole convert(Long id) {
        return rolesRepo.findById(id).orElse(null);
    }   
}