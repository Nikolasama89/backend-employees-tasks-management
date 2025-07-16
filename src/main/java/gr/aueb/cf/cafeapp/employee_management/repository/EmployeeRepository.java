package gr.aueb.cf.cafeapp.employee_management.repository;

import gr.aueb.cf.cafeapp.employee_management.core.enums.JobTitle;
import gr.aueb.cf.cafeapp.employee_management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findByRegionId(Long id);
    Optional<Employee> findByVat(String vat);
    List<Employee> findByJobTitle(JobTitle jobTitle);
}
