package gr.aueb.cf.cafeapp.employee_management.controller;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with username, password and role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping
    public ResponseEntity<UserReadOnlyDTO> createUser(@RequestBody @Valid UserInsertDTO insertDTO) throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        UserReadOnlyDTO created = userService.insertUser(insertDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users retrieved")
    })
    @GetMapping
    public ResponseEntity<List<UserReadOnlyDTO>> getAllUsers() {
        List<UserReadOnlyDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(
            summary = "Get a user by username",
            description = "Retrieves a single user by their username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserReadOnlyDTO> getUserByUsername(@PathVariable String username) throws EntityNotFoundException {
        UserReadOnlyDTO user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a user by ID",
            description = "Deletes the user with the given ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws EntityNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
