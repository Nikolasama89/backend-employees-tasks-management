package gr.aueb.cf.cafeapp.employee_management.controller;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.dto.UserInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
