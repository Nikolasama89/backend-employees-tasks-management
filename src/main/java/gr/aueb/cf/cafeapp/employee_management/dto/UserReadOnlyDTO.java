package gr.aueb.cf.cafeapp.employee_management.dto;

import gr.aueb.cf.cafeapp.employee_management.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserReadOnlyDTO {

    private String username;

    private String role;
}
