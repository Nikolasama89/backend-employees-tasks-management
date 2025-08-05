package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.UserInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserReadOnlyDTO;

import java.util.List;

public interface IUserService {

    UserReadOnlyDTO insertUser(UserInsertDTO insertDTO);

    List<UserReadOnlyDTO> getAllUsers();

    UserReadOnlyDTO getUserByUsername(String username) ;

    void deleteUser(Long id);

}
