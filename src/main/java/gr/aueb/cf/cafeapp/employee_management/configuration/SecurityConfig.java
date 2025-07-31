package gr.aueb.cf.cafeapp.employee_management.configuration;

import gr.aueb.cf.cafeapp.employee_management.core.enums.Role;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ΓΙΑ ΝΑ ΠΑΙΡΝΕΙ ΤΟΝ ΑΡΧΙΚΟ ΜΑ
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    // DISABLING AUTH FOR TESTING ENDPOINTS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                // Swagger/OpenAPI UI & docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Users: ΜΟΝΟ ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")

                        // Employees:
                        // Δημιουργία/Ενημέρωση/Διαγραφή μόνο ADMIN
                        // Ανάγνωση (GET) επιτρέπεται και σε EMPLOYEE
                        .requestMatchers(HttpMethod.POST, "/api/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAnyRole("ADMIN", "EMPLOYEE")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());  // ενεργοποιηση Basic Auth
        return http.build();
    }

    // ΓΙΑ ΝΑ ΕΧΟΥΜΕ ΕΝΑΝ ΑΡΧΙΚΟ ADMIN ΟΤΑΝ ΦΟΡΤΩΝΕΙ Η ΕΦΑΡΜΟΓΗ
    @Bean
    CommandLineRunner seedAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                User user = new User();
                user.setUsername("admin");
                user.setPassword(encoder.encode("Admin@123"));
                user.setRole(Role.ADMIN);
                repo.save(user);
            }
        };
    }
}
