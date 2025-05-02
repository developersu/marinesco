package ru.redrise.marinesco.security;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
public class UserRole implements GrantedAuthority{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Type type;

    @Override
    public String getAuthority() {
        if (type == null)
            throw new UnsupportedOperationException("Unimplemented method 'getAuthority'");

        return switch (type) {
            case USER -> "ROLE_USER";
            case ADMIN -> "ROLE_ADMIN";
        };
    }

    public enum Type{
        USER, ADMIN
    }
}
