package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.core.enums.JobTitle;
import gr.aueb.cf.cafeapp.employee_management.core.exceptions.EntityAlreadyExistsException;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
ΤΑ VALIDATION ANNOTATIONS ΠΟΥ ΕΧΟΥΜΕ ΔΕΝ ΤΡΕΧΟΥΝ ΣΤΑ UNIT TESTS ΜΟΝΟ ΣΤΟΥΣ CONTROLLERS
 */


@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    // ΚΑΝΟΥΜΕ MOCK ΤΙΣ ΕΞΑΡΤΗΣΕΙΣ ΓΙΑ ΝΑ ΤΕΣΤΑΡΙΣΤΕΙ Η ΛΟΓΙΚΗ ΤΟΥ SERVICE
    @Mock private EmployeeRepository employeeRepository;
    @Mock private RegionRepository regionRepository;
    @Mock private UserRepository userRepository;
    @Mock private Mapper mapper;

    // INJECT TA MOCKS
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ΔΗΜΙΟΥΡΓΙΑ DTOS ΓΙΑ ΤΑ ΣΕΝΑΡΙΑ ΠΟΥ ΕΧΟΥΜΕ
    private EmployeeInsertDTO insertDTO() {
        EmployeeInsertDTO dto = new EmployeeInsertDTO();
        dto.setFirstname("Nick");
        dto.setLastname("Mich");
        dto.setVat("123456789");
        dto.setPhone("2100000000");
        dto.setEmail("nik@example.com");
        dto.setRegionId(10L);
        dto.setUserId(100L);
        dto.setJobTitle(JobTitle.values()[0]);
        return dto;
    }

    private EmployeeUpdateDTO updateDTO() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setId(1L);  // ΑΝ ΕΙΝΑΙ NULL ΣΚΑΕΙ ERROR ΣΤΟ TEST
        dto.setFirstname("Nick");
        dto.setLastname("Mich");
        dto.setVat("123456789");
        dto.setPhone("2100000000");
        dto.setEmail("nik@example.com");
        dto.setJobTitle(JobTitle.values()[0]);
        dto.setRegionId(10L);   // ΟΠΩΣ ΚΑΙ ΑΥΤΟ
        return dto;
    }

    // insert success scenario
    @Test
    void insertEmployeeSuccess() throws Exception {
        // ΣΤΗΝΟΥΜΕ ΤΑ stubs(ΡΥΘΜΙΣΗ ΣΥΜΠΕΡΙΦΟΡΑΣ-ΣΤΗΣΙΜΟ ΣΚΗΝΙΚΟΥ) για το σεναριο του insert
        var dto = insertDTO();
        var region = new Region();
        var user = new User();
        var entity = new Employee();
        var saved = new Employee();
        var read = new EmployeeReadOnlyDTO();

        // STUBS
        when(employeeRepository.findByVat("123456789")).thenReturn(Optional.empty());
        when(regionRepository.findById(10L)).thenReturn(Optional.of(region));
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(mapper.mapToEmployeeEntity(dto, region, user)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(saved);
        when(mapper.mapToEmployeeReadOnlyDTO(saved)).thenReturn(read);

        // ΚΑΛΟΥΜΕ ΤΟ SERVICE
        var result = employeeService.insertEmployee(dto);

        // ΕΛΕΓΧΟΣ ME ASSERT ΟΤΙ ΕΓΙΝΑΝ ΟΙ ΣΩΣΤΕΣ ΚΛΗΣΕΙΣ
        assertThat(result).isSameAs(read);
        verify(employeeRepository).findByVat("123456789");
        verify(regionRepository).findById(10L);
        verify(userRepository).findById(100L);
        verify(employeeRepository).save(entity);
        verify(mapper).mapToEmployeeReadOnlyDTO(saved);
    }

    // insert error scenario-DUPLICATE VAT
    @Test
    void insertEmployeeDuplicateVat() {
        var dto = insertDTO();
        when(employeeRepository.findByVat("123456789")).thenReturn(Optional.of(new Employee()));

        assertThrows(EntityAlreadyExistsException.class, () -> employeeService.insertEmployee(dto));

        // ΑΝ ΥΠΑΡΧΕΙ ΔΙΠΛΟ VAT ΔΕΝ ΠΑΜΕ ΚΑΘΟΛΟΥ ΣΤΑ region, user, mapper.
        verify(employeeRepository).findByVat("123456789");
        verifyNoMoreInteractions(regionRepository, userRepository, mapper);
    }

    // update success scenario
    @Test
    void updateEmployeeSuccess() {
        var dto = updateDTO();
        var employee = new Employee();
        var region = new Region();
        var saved = new Employee();
        var read = new EmployeeReadOnlyDTO();

        // eq ειναι matcher του Mockito που περιμενει ακριβως την τιμη
        when(employeeRepository.findById(eq(dto.getId()))).thenReturn(Optional.of(employee));
        when(regionRepository.findById(eq(dto.getRegionId()))).thenReturn(Optional.of(region));
        when(employeeRepository.save(employee)).thenReturn(saved);
        when(mapper.mapToEmployeeReadOnlyDTO(saved)).thenReturn(read);

        var result = employeeService.updateEmployee(dto);

        assertThat(result).isSameAs(read);
        verify(mapper).updateEmployeeFromDTO(dto, employee, region);
        verify(employeeRepository).save(employee);
    }

    // update fail scenario with not found employee
    @Test
    void updateEmployeeEmployeeNotFound() {
        var dto = updateDTO();
        when(employeeRepository.findById(eq(dto.getId()))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.updateEmployee(dto));

        verify(employeeRepository).findById(1L);
        // ΔΕΝ ΣΥΝΕΧΙΖΕΙ ΣΕ region, mapper, save ΑΝ ΔΕΝ ΒΡΕΙ ΤΟΝ employee.
        verify(regionRepository, never()).findById(anyLong());
        verify(mapper, never()).updateEmployeeFromDTO(any(), any(), any());
        verify(employeeRepository, never()).save(any());
    }
}
