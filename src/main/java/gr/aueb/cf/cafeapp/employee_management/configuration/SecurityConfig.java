package gr.aueb.cf.cafeapp.employee_management.configuration;

import gr.aueb.cf.cafeapp.employee_management.authentication.JwtAuthenticationFilter;
import gr.aueb.cf.cafeapp.employee_management.core.enums.Role;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.repository.UserRepository;
import gr.aueb.cf.cafeapp.employee_management.security.JwtService;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Κεντρική ρύθμιση ασφάλειας για την εφαρμογή.
 *
 * Stateless security με JWT (χωρίς server-side sessions).
 * CORS επιτρέπεται από το frontend (localhost:4200).
 * Συγκεκριμένα endpoints είναι public (auth, swagger).
 * Όλα τα υπόλοιπα απαιτούν αυθεντικοποίηση και role-based έλεγχο.
 */

@Configuration
public class SecurityConfig {

    /**
     * Bean του JWT φίλτρου μας ώστε να μπορεί να ενσωματωθεί στο security chain.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    /**
     * Password encoder: BCrypt (ασφαλής, με salt + cost factor).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * UserDetailsService: βρίσκει χρήστες από τη βάση μέσω του repo
     * Απαραίτητο για το authentication flow του Spring Security
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    /**
     * Κύρια αλυσίδα φίλτρων (SecurityFilterChain).
     *
     * - CORS on
     * - CSRF off (γιατί είμαστε stateless/JWT)
     * - Authorization κανόνες ανά endpoint/method
     * - Ενσωμάτωση του JWT φίλτρου πριν το UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                // ΕΠΙΤΡΕΠΕΙ CORS
                .cors(Customizer.withDefaults())
                // ME JWT ΔΕΝ ΚΡΑΤΑΜΕ CSRF TOKENS(ΔΕΝ ΕΧΟΥΜΕ SERVER SESSION)
                .csrf(AbstractHttpConfigurer::disable)
                // ΚΑΝΕΝΑ HTTP SESSION- ΚΑΘΕ REQUEST ΑΠΟΔΕΙΚΝΥΕΙ ΠΟΙΟΣ ΕΙΝΑΙ ΜΕ ΤΟ JWT
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // AUTH RULES
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
                        // ΟΤΙ ΑΛΛΟ ΔΕΝ ΟΟΡΙΣΤΗΚΕ ΘΕΛΕΙ AUTHENTICATION
                        .anyRequest().authenticated()
                )
//                .httpBasic(Customizer.withDefaults())
                // ΒΑΖΟΥΜΕ JWT ΦΙΛΤΡΟ ΠΡΙΝ ΤΟ DEFAULT USERNAME/PASSWORD
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

    /**
     * Παρέχει το AuthenticationManager του Spring χρειάζεται στην AuthenticationService
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // CORS CONFIGURATION
//    @Bean
//    public WebMvcConfigurer corsConfig() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")  // ΕΠΙΤΡΕΠΕΙ CORS ΣΕ ΟΛΑ ΤΑ ENDPOINTS
//                        .allowedOrigins("http://localhost:4200")    // ANGULAR
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")    // METHODS
//                        .allowedHeaders("*")    // ALLOWS ALL HEADERS(NEEDS FOR AUTHORIZATION)
//                        .allowCredentials(true);    // ALLOWS COOKIES/AUTH HEADERS(JWT)
//            }
//        };
//    }

    // CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        config.setAllowCredentials(true); // μόνο αν όντως στέλνεις cookies/credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
