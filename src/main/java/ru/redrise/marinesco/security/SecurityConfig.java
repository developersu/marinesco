package ru.redrise.marinesco.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import ru.redrise.marinesco.User;
import ru.redrise.marinesco.data.UserRepository;

@Configuration
//@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return username -> {
            User user = repository.findByUsername(username);
            if (user != null)
                return user;
            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        return http
                .authorizeHttpRequests(autorize -> autorize
                        .requestMatchers(mvc.pattern("/favicon.ico")).permitAll()
                        .requestMatchers(mvc.pattern("/jquery.js")).permitAll()
                        .requestMatchers(mvc.pattern("/styles/**")).permitAll()
                        .requestMatchers(mvc.pattern("/images/*")).permitAll()
                        .requestMatchers(mvc.pattern("/register")).permitAll()
                        .requestMatchers(mvc.pattern("/login")).anonymous()
                        .requestMatchers(mvc.pattern("/error")).permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(mvc.pattern("/")).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(mvc.pattern("/profile/**")).hasAnyRole("ADMIN", "USER")
                        //.requestMatchers(mvc.pattern("/design/**")).hasRole("USER")
                        .anyRequest().authenticated())
                        //.anyRequest().permitAll())
                .formLogin(formLoginConfigurer -> formLoginConfigurer
                        .loginPage("/login")
                        //.loginProcessingUrl("/auth")
                        .usernameParameter("login")
                        .passwordParameter("pwd")
                        .defaultSuccessUrl("/")
                        )
//                    .formLogin(Customizer.withDefaults())
//.oauth2Login(c -> c.loginPage("/login"))
                .logout(Customizer.withDefaults())
                /* Make temporary access to H2 infrastructure START*/
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers(PathRequest.toH2Console())
                        .disable()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                /* Make temporary access to H2 infrastructure END*/
                .build();
    }
}
