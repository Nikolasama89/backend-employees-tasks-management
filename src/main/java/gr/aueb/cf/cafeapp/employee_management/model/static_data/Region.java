package gr.aueb.cf.cafeapp.employee_management.model.static_data;

import gr.aueb.cf.cafeapp.employee_management.model.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "regions")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // ΣΥΝΔΕΟΥΜΕ ΜΕ ΤΟΥΣ TEACHERS ME SET ΠΟΥ ΕΧΟΥΝ ΠΟΛΥ ΚΑΛΟΥΣ ΧΡΟΝΟΥΣ ΑΝΑΖΗΤΗΣΗΣ
    @Getter(AccessLevel.PUBLIC)
    @OneToMany(mappedBy = "region") // ΕΔΩ ΠΑΙΡΝΕΙ ΤΟ ΟΝΟΜΑ ΑΠΟ ΤΟ ENTITY ΠΟΥ ΕΙΜΑΣΤΕ ΔΛΔ REGION
    @com.fasterxml.jackson.annotation.JsonIgnore // ΓΙΑ ΝΑ ΚΟΠΕΙ Ο ΚΥΚΛΟΣ ΤΗΣ ΚΥΚΛΙΚΗΣ ΑΝΑΦΟΡΑΣ
    private Set<Employee> employees = new HashSet<>();    // ΑΝ ΔΕΝ ΚΑΝΟΥΜΕ HASHSET TO HIBERNATE ΚΑΝΕΙ ΑΠΟ ΜΟΝΟ ΤΟΥ ΚΑΙ ΔΕΝ ΕΙΝΑΙ HASHSET


}
