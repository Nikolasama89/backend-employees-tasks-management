package gr.aueb.cf.cafeapp.employee_management.mapper;

import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeUpdateDTO;
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
                employee.getPhone(),
                employee.getVat(),
                employee.getEmail(),
                employee.getRegion().getId(),
                employee.getRegion().getName(),
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

}
