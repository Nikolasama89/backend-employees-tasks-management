package gr.aueb.cf.cafeapp.employee_management.configuration;

import gr.aueb.cf.cafeapp.employee_management.authentication.AuthenticationService;
import gr.aueb.cf.cafeapp.employee_management.authentication.JwtAuthenticationFilter;
import gr.aueb.cf.cafeapp.employee_management.core.enums.Role;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationService authService;


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
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // public auth endpoint
                        .requestMatchers("/api/auth/**").permitAll()

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
//                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
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

    // ΔΗΜΙΟΥΡΓΟΥΜΕ ΤΟ BEAN ΓΙΑ ΝΑ ΕΧΟΥΜΕ ΠΡΟΣΒΑΣΗ ΣΤΟ ΕΙΔΙΚΟ SPRING-ΕΡΓΑΛΕΙΟ ΕΛΕΓΧΟΥ ΤΑΥΤΟΤΗΤΑΣ ΜΕΣΑ ΣΤΙΣ ΚΛΑΣΕΙΣ ΜΑΣ.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
