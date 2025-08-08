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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );

            log.info("User {} successfully authenticated", dto.getUsername());

            User user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new EntityNotFoundException("User not Found"));

            String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

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
