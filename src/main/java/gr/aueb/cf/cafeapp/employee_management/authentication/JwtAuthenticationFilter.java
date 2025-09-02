package gr.aueb.cf.cafeapp.employee_management.authentication;

import gr.aueb.cf.cafeapp.employee_management.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Φίλτρο JWT που τρέχει μία φορά ανά request.
 *
 * Διαβάζει το Authorization header (Bearer <token>)
 * Βγάζει username από το JWT
 * Φορτώνει τα UserDetails του χρήστη
 * Αν το token είναι έγκυρο, «συνδέει» τον χρήστη στο SecurityContextHolder
 * Αν το token έχει λήξει -> 401. Αν είναι προβληματικό -> 403. Επιστρέφει μικρό JSON για τον client
 *
 * Το φιλτράρισμα είναι idempotent (OncePerRequestFilter)
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Εδώ γίνεται όλη η δουλειά της εξαγωγής/ελέγχου του JWT και αν όλα είναι καλά
     * γίνεται η τοποθέτηση του Authentication στο SecurityContext.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ΠΑΙΡΝΟΥΜΕ ΤO AUTHORIZATION HEADER
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // ΑΝ ΛΕΙΠΕΙ Η ΔΕΝ ΞΕΚΙΝΑΕΙ ΑΠΟ "BEARER" ΔΕΝ ΚΑΝΟΥΜΕ ΤΠΤ ΣΥΝΕΧΙΖΟΥΜΕ ΤΟ CHAIN
        // ΤΑ PUBLIC ENDPOINTS ΣΥΝΕΧΙΖΟΥΝ ΚΑΝΟΝΙΚΑ ΧΩΡΙΣ ΤΟΚΕΝ
        // PROTECTED ΘΑ ΠΕΡΑΣΟΥΝ ΣΤΟ SECURITY ΚΑΙ ΘΑ ΚΟΠΟΥΝ ΕΚΕΙ ΑΝ ΔΕΝ ΕΧΟΥΜΕ TOKEN
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ΚΟΒΟΥΜΕ "BEARER" ΚΑΙ ΚΡΑΤΑΜΕ ΤΟ ΤΟΚΕΝ
        jwt = authHeader.substring(7);

        try {
            // EXTRACT USERNAME
            username = jwtService.extractSubject(jwt);

            System.out.println("www JWT extracted.. ID: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // ΦΕΡΝΟΥΜΕ ΣΤΟΙΧΕΙΑ ΤΟΥ ΧΡΗΣΤΗ
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // ΕΛΕΓΧΟΥΜΕ ΤΟΚΕΝ (ΥΠΟΓΡΑΦΗ, ΛΗΞΗ)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // ΧΤΙΣΙΜΟ AUTHENTICATION KAI ΠΕΡΝΑΜΕ ΣΤΟ SECURITY CONTEXT
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("Token is NOT valid: " + request.getRequestURI());
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("Warn: Expired Token", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            String jsonBody = "{\"code\": \"expiredToken\", \"message\": \"" + e.getMessage() + "\"}";
            response.getWriter().write(jsonBody);
            return;     // ΔΕΝ ΣΥΝΕΧΙΖΕΙ ΤΟ CHAIN
        } catch (Exception e) {
            log.warn("Error: something went wrong while parsing JWT", e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            String jsonBody = "{\"code\": \"invalidToken\", \"description\": \"" + e.getMessage() + "\"}";
            response.getWriter().write(jsonBody);
            return;
        }

        // ΑΝ ΔΕΝ ΕΧΟΥΜΕ ΣΦΑΛΜΑΤΑ ΠΕΡΝΑΕΙ ΤΟ TO REQUEST ΚΑΙ ΣΥΝΕΧΙΖΕΙ
        filterChain.doFilter(request, response);
    }
}
