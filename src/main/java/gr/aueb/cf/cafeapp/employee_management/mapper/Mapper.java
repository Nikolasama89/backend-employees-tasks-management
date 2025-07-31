package gr.aueb.cf.cafeapp.employee_management.mapper;

import gr.aueb.cf.cafeapp.employee_management.core.enums.Role;
import gr.aueb.cf.cafeapp.employee_management.dto.*;
import gr.aueb.cf.cafeapp.employee_management.model.Employee;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.model.static_data.Region;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public Employee mapToEmployeeEntity(EmployeeInsertDTO employeeInsertDTO, Region region, User user) {
        Employee employee = new Employee();
        employee.setFirstname(employeeInsertDTO.getFirstname());
        employee.setLastname(employeeInsertDTO.getLastname());
        employee.setVat(employeeInsertDTO.getVat());
        employee.setEmail(employeeInsertDTO.getEmail());
        employee.setPhone(employeeInsertDTO.getPhone());
        employee.setJobTitle(employeeInsertDTO.getJobTitle());
        employee.setRegion(region);
        employee.setUserId(user);
        return employee;
    }

    public EmployeeReadOnlyDTO mapToEmployeeReadOnlyDTO(Employee employee) {
        return new EmployeeReadOnlyDTO(employee.getId(), employee.getCreatedAt(),
                employee.getUpdatedAt(),
                employee.getUuid(),
                employee.getFirstname(),
                employee.getLastname(),
                employee.getVat(),
                employee.getPhone(),
                employee.getRegion().getName(),
                employee.getRegion().getId(),
                employee.getEmail(),
                employee.getUserId().getId(),
                employee.getJobTitle());
    }

    public void updateEmployeeFromDTO(EmployeeUpdateDTO updateDTO, Employee employee, Region region) {
        employee.setFirstname(updateDTO.getFirstname());
        employee.setLastname(updateDTO.getLastname());
        employee.setEmail(updateDTO.getEmail());
        employee.setPhone(updateDTO.getPhone());
        employee.setJobTitle(updateDTO.getJobTitle());
        employee.setRegion(region);
    }

    public User mapToUserEntity(UserInsertDTO userInsertDTO) {
        return new User(null, userInsertDTO.getUsername(), userInsertDTO.getPassword(), userInsertDTO.getRole());
    }

    public UserReadOnlyDTO mapUserToReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(user.getUsername(), user.getRole().name());
    }

}
