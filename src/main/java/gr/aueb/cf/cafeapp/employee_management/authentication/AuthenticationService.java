package gr.aueb.cf.cafeapp.employee_management.authentication;

import gr.aueb.cf.cafeapp.employee_management.dto.AuthenticationRequestDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.AuthenticationResponseDTO;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.repository.UserRepository;
import gr.aueb.cf.cafeapp.employee_management.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Υπηρεσία αυθεντικοποίησης χρηστών.
 * 1) Ελέγχει username/password μέσω του AuthenticationManager
 * 2) Φορτώνει τον χρήστη από τη βάση δεδομένων
 * 3) Εκδίδει JWT που περιέχει το username και το role
 * 4) Επιστρέφει ένα απλό response με username + token
 * Αν τα στοιχεία είναι λάθος, το Spring πετάει AuthenticationException
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {
        try {
            // ΖΗΤΑΜΕ ΑΠΟ ΤΟ SPRING ΝΑ AUTHENTICATE ΤΑ ΣΤΟΙΧΕΙΑ ΣΥΝΔΕΣΗΣ
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );

            log.info("User {} successfully authenticated", dto.getUsername());

            // ΦΟΡΤΩΝΟΥΜΕ ΧΡΗΣΤΗ ΑΠΟ ΤΗΝ ΒΑΣΗ ΜΕ ΒΑΣΗ ΤΟ ΟΝΟΜΑ ΠΟΥ ΕΚΑΝΕ AUTH ΤΟ SPRING
            User user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new EntityNotFoundException("User not Found"));

            // ΕΚΔΟΣΗ ΤΟΚΕΝ - CLAIMS ΕΙΝΑΙ ΤΟ USERNAME KAI O ΡΟΛΟΣ
            String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

            // ΕΠΙΣΤΡΟΦΗ ΟΤΙ ΧΡΕΙΑΖΕΤΑΙ Ο CLIENT ΓΙΑ ΤΑ ΕΠΟΜΕΝΑ REQUESTS
            return new AuthenticationResponseDTO(
                    user.getUsername(),
                    token
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", dto.getUsername(), e.getMessage());
            throw e;
        }

    }
}
