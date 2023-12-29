package ru.redrise.marinesco;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import ru.redrise.marinesco.security.UserRole;

@Data
@Entity
@Table(name = "\"USER\"")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class User implements UserDetails{
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique=true)
    //@LoginOccupiedConstraint
    private final String username;
    private String password;
    private String displayname;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private final List<UserRole> authorities;

    public User(String username, String password, String displayname, List<UserRole> authorities){
        this.username = username;
        this.password = password;
        this.displayname = displayname;
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setRole(UserRole role){ // TODO
        this.authorities.add(role);
    }

    public boolean isAdmin(){
        return authorities.get(0).getAuthority().equals("ROLE_ADMIN");
    }
}
