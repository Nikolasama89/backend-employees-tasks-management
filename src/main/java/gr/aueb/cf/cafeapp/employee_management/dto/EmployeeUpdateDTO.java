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
public class EmployeeUpdateDTO {

    @NotNull(message = "Ο κωδικός δεν μπορεί να μην υπάρχει.")
    private Long id;

    @NotNull(message = "Το όνομα δεν μπορεί να μην υπάρχει.")
    @Size(min = 2, max = 255, message = "Το όνομα πρέπει να είναι μεταξύ 2-255 χαρακτήρων.")
    private String firstname;

    @NotNull(message = "Το επώνυμο δεν μπορεί να μην υπάρχει.")
    @Size(min = 2, max = 255, message = "Το επώνυμο πρέπει να είναι μεταξύ 2-255 χαρακτήρων.")
    private String lastname;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
    private String email;

    @NotNull(message = "Ο τίτλος δεν μπορεί να μην υπάρχει.")
    private JobTitle jobTitle;

    @NotNull(message = "Η περιοχή δεν μπορεί να μην υπάρχει.")
    private Long regionId;

}
