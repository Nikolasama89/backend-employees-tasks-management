package gr.aueb.cf.cafeapp.employee_management.controller;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeUpdateDTO;
import gr.aueb.cf.cafeapp.employee_management.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new employee", description = "Creates a new employee record.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Employee already exists")
    })
    @PostMapping
    public ResponseEntity<EmployeeReadOnlyDTO> createEmployee(@RequestBody @Valid EmployeeInsertDTO insertDTO) throws EntityInvalidArgumentException, EntityAlreadyExistsException {
        EmployeeReadOnlyDTO created = employeeService.insertEmployee(insertDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing employee", description = "Updates the employee with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeReadOnlyDTO> updateEmployee(@RequestBody @Valid EmployeeUpdateDTO updateDTO, @PathVariable Long id) throws EntityInvalidArgumentException, EntityNotFoundException {
        updateDTO.setId(id);
        EmployeeReadOnlyDTO updated = employeeService.updateEmployee(updateDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @Operation(summary = "Delete an employee", description = "Deletes the employee with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) throws EntityNotFoundException {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get an employee by ID", description = "Retrieves the employee with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeReadOnlyDTO> getEmployeeById(@PathVariable Long id) throws EntityNotFoundException {
        EmployeeReadOnlyDTO employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of employees retrieved")
    })
    @GetMapping
    public ResponseEntity<List<EmployeeReadOnlyDTO>> getAllEmployees() {
        List<EmployeeReadOnlyDTO> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Operation(summary = "Get paginated employees", description = "Retrieves a paginated list of employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of employees retrieved")
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<EmployeeReadOnlyDTO>> getPaginatedEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<EmployeeReadOnlyDTO> employeesPage = employeeService.getAllEmployeesPaginated(page, size);
        return new ResponseEntity<>(employeesPage, HttpStatus.OK);
    }

}
