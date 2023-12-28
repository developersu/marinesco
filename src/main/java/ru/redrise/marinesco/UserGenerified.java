package ru.redrise.marinesco;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.redrise.marinesco.security.UserRole;

@Data
@NoArgsConstructor
public class UserGenerified {

    private static final long serialVersionUID = 1L;
    
    private Long id;

    private String name;
    private String displayName;
    private List<UserRole> role;

    public UserGenerified(User user){
        this.id = user.getId();
        this.name = user.getUsername();
        this.displayName = user.getDisplayname();
        this.role = user.getAuthorities();
    }
}