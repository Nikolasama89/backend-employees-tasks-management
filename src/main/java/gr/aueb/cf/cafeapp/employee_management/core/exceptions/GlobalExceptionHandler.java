package gr.aueb.cf.cafeapp.employee_management.core.exceptions;

import gr.aueb.cf.cafeapp.employee_management.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<ErrorResponseDTO> handleAuthError(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("AuthenticationFailed", ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("AccessDenied", "You do not have permission to perform this action"),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(EntityNotFoundException e) {
        ErrorResponseDTO body = new ErrorResponseDTO(e.getCode(), e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityInvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidArg(EntityInvalidArgumentException ex) {
        ErrorResponseDTO body = new ErrorResponseDTO(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(EntityAlreadyExistsException ex) {
        ErrorResponseDTO body = new ErrorResponseDTO(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Για τα @Valid των DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a,b) -> a + "; " + b)
                .orElse(ex.getMessage());
        ErrorResponseDTO body = new ErrorResponseDTO("ValidationError", msg);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponseDTO body = new ErrorResponseDTO("ConstraintViolation", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAll(Exception ex) {
        ErrorResponseDTO body = new ErrorResponseDTO("ServerError", "Something went wrong, try again!");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
