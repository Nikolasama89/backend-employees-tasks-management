package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.UserInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.UserReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.mapper.Mapper;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements IUserService{

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, Mapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserReadOnlyDTO insertUser(UserInsertDTO insertDTO) throws EntityAlreadyExistsException, EntityInvalidArgumentException {

        if (insertDTO == null) {
            throw new EntityInvalidArgumentException("User", "InsertDTO cannot be null");
        }

        String username = insertDTO.getUsername();
        if (username == null || username.isBlank()) {
            throw new EntityInvalidArgumentException("User", "Username cannot be blank");
        }

        String rawPassword = insertDTO.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new EntityInvalidArgumentException("User", "Password cannot be blank");
        }

        if (insertDTO.getRole() == null) {
            throw new EntityInvalidArgumentException("User", "Role must be specified");
        }

        if (userRepository.findByUsername(insertDTO.getUsername()).isPresent()){
            log.warn("Attempting to insert duplicate user: {}", insertDTO.getUsername());
            throw new EntityAlreadyExistsException("User", "User with username: " + insertDTO.getUsername() + " already exists");
        }
        User user = mapper.mapToUserEntity(insertDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);

        return mapper.mapUserToReadOnlyDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserReadOnlyDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mapper::mapUserToReadOnlyDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserReadOnlyDTO getUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with username: " + username + " not found."));
        return mapper.mapUserToReadOnlyDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) throws EntityNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", "User not found"));
        userRepository.delete(user);
        log.info("Deleted user with id={}", id);
    }
}
