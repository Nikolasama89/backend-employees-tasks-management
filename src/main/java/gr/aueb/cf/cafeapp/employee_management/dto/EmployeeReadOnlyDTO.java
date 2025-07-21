package gr.aueb.cf.cafeapp.employee_management.dto;

import gr.aueb.cf.cafeapp.employee_management.core.enums.JobTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeReadOnlyDTO {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String uuid;
    private String firstname;
    private String lastname;
    private String vat;
    private String region;
    private String phone;
    private Long regionId;
    private String email;
    private Long userId;
    private JobTitle jobTitle;
}
