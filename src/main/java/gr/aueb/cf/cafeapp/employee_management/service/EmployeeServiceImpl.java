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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

            if (employeeRepository.findByVat(insertDTO.getVat()).isPresent()) {
                log.warn("Insert failed: duplicate VAT {}", insertDTO.getVat());
                throw new EntityAlreadyExistsException("Employee", "Employee with vat " + insertDTO.getVat() + " already exists.");
            }

            Region region = regionRepository.findById(insertDTO.getRegionId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Region", "Region with id " + insertDTO.getRegionId() + " not found."));

            User user = userRepository.findById(insertDTO.getUserId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("User", "User with ID: " + insertDTO.getUserId() + " not found."));

            Employee employee = mapper.mapToEmployeeEntity(insertDTO, region, user);
            Employee savedEmployee = employeeRepository.save(employee);

            return mapper.mapToEmployeeReadOnlyDTO(savedEmployee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeReadOnlyDTO updateEmployee(EmployeeUpdateDTO updateDTO) {

            Employee employee = employeeRepository.findById(updateDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Employee", "Employee with id " + updateDTO.getId() + " not found"));

            Region region = regionRepository.findById(updateDTO.getRegionId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Region", "Region with id " + updateDTO.getRegionId() + " not found"));

            mapper.updateEmployeeFromDTO(updateDTO, employee, region);

            Employee updatedEmployee = employeeRepository.save(employee);
            return mapper.mapToEmployeeReadOnlyDTO(updatedEmployee);

    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {

            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Employee", "Employee with id: " + id + " not found"));
            employeeRepository.delete(employee);
            log.info("Employee with id={} deleted", id);
    }

    @Override
    public EmployeeReadOnlyDTO getEmployeeById(Long id){
        return employeeRepository.findById(id)
                .map(mapper::mapToEmployeeReadOnlyDTO)
                .orElseThrow(() ->
                        new EntityNotFoundException("Employee", "Employee with id: " + id + " not found")
                );
    }

    @Override
    public List<EmployeeReadOnlyDTO> getAllEmployees() {
            List<EmployeeReadOnlyDTO> readOnlyDTOS = employeeRepository.findAll()
                    .stream()
                    .map(mapper::mapToEmployeeReadOnlyDTO)
                    .toList();
            return readOnlyDTOS;
    }

    @Override
    public Page<EmployeeReadOnlyDTO> getAllEmployeesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable)
                .map(mapper::mapToEmployeeReadOnlyDTO);
    }

    //    @Override
//    public List<EmployeeReadOnlyDTO> getEmployeesByCriteria(Map<String, Object> criteria) {
//        List<EmployeeReadOnlyDTO> readOnlyDTOS = employeeRepository.
//    }

//    @Override
//    public List<EmployeeReadOnlyDTO> getEmployeesByCriteriaPaginated(Map<String, Object> criteria, Integer page, Integer size) {
//        return List.of();
//    }
}
