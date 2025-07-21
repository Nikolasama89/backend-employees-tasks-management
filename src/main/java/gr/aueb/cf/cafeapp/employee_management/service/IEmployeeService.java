package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeUpdateDTO;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {
    EmployeeReadOnlyDTO insertEmployee(EmployeeInsertDTO insertDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    EmployeeReadOnlyDTO updateEmployee(EmployeeUpdateDTO updateDTO)
            throws EntityNotFoundException, EntityInvalidArgumentException;

    void deleteEmployee(Object id) throws EntityNotFoundException;

    EmployeeReadOnlyDTO getEmployeeById(Object id) throws EntityNotFoundException;

    List<EmployeeReadOnlyDTO> getAllEmployees();

//    long getEmployeesCountByCriteria(Map<String, Object> criteria);

    List<EmployeeReadOnlyDTO> getEmployeesByCriteria(Map<String, Object> criteria);

    List<EmployeeReadOnlyDTO> getEmployeesByCriteriaPaginated(Map<String, Object> criteria, Integer page, Integer size);
}
