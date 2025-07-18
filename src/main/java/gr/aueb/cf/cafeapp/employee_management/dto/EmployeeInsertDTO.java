package gr.aueb.cf.cafeapp.employee_management.dto;

import gr.aueb.cf.cafeapp.employee_management.core.enums.JobTitle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeInsertDTO {

    @NotNull(message = "Name can not be null")
    @Size(min = 2, message = "Name should contain at least 2 characters")
    private String firstname;

    @NotNull(message = "Name can not be null")
    @Size(min = 2, message = "Name should contain at least 2 characters")
    private String lastname;

    @Pattern(regexp = "\\d{9}", message = "Vat must be exactly 9 digits.")
    private String vat;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
    private String email;

    @NotNull(message = "Area can not be null")
    private Long regionId;

    @NotNull(message = "Employee must have a job")
    private JobTitle jobTitle;

    @NotNull(message = "Area can not be null")
    private Long userId;
}
