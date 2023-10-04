package ru.redrise.marinesco;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.RolesRepository;
import ru.redrise.marinesco.data.UserRepository;
import ru.redrise.marinesco.security.UserRole;

@Slf4j
@Configuration
public class ShinyApplicationRunner {
    private UserRepository users;
    private RolesRepository roles;

    public ShinyApplicationRunner(UserRepository users, RolesRepository roles) {
        this.users = users;
        this.roles = roles;
    }

    @Bean
    public ApplicationRunner appRunner() {
        return args -> {
            if (isFirstRun()) {
                setRoles();
                setAdmin(args);
            } else
                log.info("NOT FIRST APPLICATION RUN; DB Already set up");
        };
    }

    private boolean isFirstRun() {
        return (roles.count() <= 0);
    }

    private void setRoles() {
        roles.saveAll(Arrays.asList(
                new UserRole(null, "Admin", UserRole.Type.ADMIN),
                new UserRole(null, "User", UserRole.Type.USER)));
    }

    private void setAdmin(ApplicationArguments args) {
        List<String> login = args.getOptionValues("admin_login");
        List<String> password = args.getOptionValues("admin_password");

        List<UserRole> adminRoleOnlyAthority = roles.findByType(UserRole.Type.ADMIN);

        if (login == null || login.size() == 0 || password == null || password.size() == 0) {
            log.warn("No administrator credentials provided, using defaults:\n * Login: root\n * Password: root\n Expected: --admin_login LOGIN --admin_password PASSWORD "); // TODO: Move into properties i18n
            var adminUser = new User("root", "root", "SuperAdmin", adminRoleOnlyAthority);
            users.save(adminUser);
            return;
        }
        users.save(new User(login.get(0), password.get(0), "SuperAdmin", adminRoleOnlyAthority));
    }
}