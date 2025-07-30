package gr.aueb.cf.cafeapp.employee_management.controller;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.service.IUserService;
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

    @PostMapping
    public ResponseEntity<UserReadOnlyDTO> createUser(@RequestBody @Valid UserInsertDTO insertDTO) throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        UserReadOnlyDTO created = userService.insertUser(insertDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserReadOnlyDTO>> getAllUsers() {
        List<UserReadOnlyDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserReadOnlyDTO> getUserByUsername(@PathVariable String username) throws EntityNotFoundException {
        UserReadOnlyDTO user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws EntityNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
