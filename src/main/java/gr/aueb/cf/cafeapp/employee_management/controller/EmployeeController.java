package gr.aueb.cf.cafeapp.employee_management.controller;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeUpdateDTO;
import gr.aueb.cf.cafeapp.employee_management.service.IEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeReadOnlyDTO> createEmployee(@RequestBody @Valid EmployeeInsertDTO insertDTO) throws EntityInvalidArgumentException, EntityAlreadyExistsException {
        EmployeeReadOnlyDTO created = employeeService.insertEmployee(insertDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeReadOnlyDTO> updateEmployee(@RequestBody @Valid EmployeeUpdateDTO updateDTO, @PathVariable Long id) throws EntityInvalidArgumentException, EntityNotFoundException {
        updateDTO.setId(id);
        EmployeeReadOnlyDTO updated = employeeService.updateEmployee(updateDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeReadOnlyDTO> deleteEmployee(@PathVariable Long id) throws EntityNotFoundException {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeReadOnlyDTO> getEmployeeById(@PathVariable Long id) throws EntityNotFoundException {
        EmployeeReadOnlyDTO employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeReadOnlyDTO>> getAllEmployees() {
        List<EmployeeReadOnlyDTO> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<EmployeeReadOnlyDTO>> getPaginatedEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<EmployeeReadOnlyDTO> employeesPage = employeeService.getAllEmployeesPaginated(page, size);
        return new ResponseEntity<>(employeesPage, HttpStatus.OK);
    }

}
