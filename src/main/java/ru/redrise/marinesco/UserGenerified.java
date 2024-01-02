package ru.redrise.marinesco;

import java.util.List;
import java.util.stream.Collectors;

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
    private List<UserRole> athorities;
    private List<UserRole> athoritiesLost;

    public UserGenerified(User user, List<UserRole> allRolesList){
        this.id = user.getId();
        this.name = user.getUsername();
        this.displayName = user.getDisplayname();
        this.athorities = user.getAuthorities();
        athoritiesLost = allRolesList.stream()
                    .filter(element -> !athorities.contains(element))
                    .collect(Collectors.toList());
    }
}