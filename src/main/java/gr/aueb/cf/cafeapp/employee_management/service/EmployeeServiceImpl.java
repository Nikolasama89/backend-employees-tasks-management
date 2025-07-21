package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeInsertDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeReadOnlyDTO;
import gr.aueb.cf.cafeapp.employee_management.dto.EmployeeUpdateDTO;
import gr.aueb.cf.cafeapp.employee_management.mapper.Mapper;
import gr.aueb.cf.cafeapp.employee_management.model.Employee;
import gr.aueb.cf.cafeapp.employee_management.model.User;
import gr.aueb.cf.cafeapp.employee_management.model.static_data.Region;
import gr.aueb.cf.cafeapp.employee_management.repository.EmployeeRepository;
import gr.aueb.cf.cafeapp.employee_management.repository.RegionRepository;
import gr.aueb.cf.cafeapp.employee_management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService{

    private final EmployeeRepository employeeRepository;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, RegionRepository regionRepository, UserRepository userRepository, Mapper mapper) {
        this.employeeRepository = employeeRepository;
        this.regionRepository = regionRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeReadOnlyDTO insertEmployee(EmployeeInsertDTO insertDTO) throws EntityAlreadyExistsException, EntityInvalidArgumentException {

        try {
            if (employeeRepository.findByVat(insertDTO.getVat()).isPresent()) {
                throw new EntityAlreadyExistsException("Employee", "Employee with vat " + insertDTO.getVat() + " already exists.");
            }

            Region region = regionRepository.findById(insertDTO.getRegionId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Region", "Region with id " + insertDTO.getRegionId() + " not found."));

            User user = userRepository.findById(insertDTO.getUserId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("User", "User with ID: " + insertDTO.getUserId() + " not found."));

            Employee employee = mapper.mapToEmployeeEntity(insertDTO, region, user);
            Employee savedEmployee = employeeRepository.save(employee);

            return mapper.mapToEmployeeReadOnlyDTO(savedEmployee);
        } catch (EntityInvalidArgumentException e) {
            log.error("Insert failed for employee with vat={}. Reason: {}", insertDTO.getVat(), e.getMessage());
            throw e;
        }
    }

    @Override
    public EmployeeReadOnlyDTO updateEmployee(EmployeeUpdateDTO updateDTO) throws EntityNotFoundException, EntityInvalidArgumentException {
        return null;
    }

    @Override
    public void deleteEmployee(Object id) throws EntityNotFoundException {

    }

    @Override
    public EmployeeReadOnlyDTO getEmployeeById(Object id) throws EntityNotFoundException {
        return null;
    }

    @Override
    public List<EmployeeReadOnlyDTO> getAllEmployees() {
        return List.of();
    }

    @Override
    public List<EmployeeReadOnlyDTO> getEmployeesByCriteria(Map<String, Object> criteria) {
        return List.of();
    }

    @Override
    public List<EmployeeReadOnlyDTO> getEmployeesByCriteriaPaginated(Map<String, Object> criteria, Integer page, Integer size) {
        return List.of();
    }
}
