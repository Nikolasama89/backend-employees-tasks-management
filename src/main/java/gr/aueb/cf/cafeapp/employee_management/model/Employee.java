package gr.aueb.cf.cafeapp.employee_management.model;

import gr.aueb.cf.cafeapp.employee_management.core.enums.JobTitle;
import gr.aueb.cf.cafeapp.employee_management.model.static_data.Region;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(unique = true)
    private String vat;

    private String firstname;
    private String lastname;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    private String email;

    @Column(name = "job_title")
    @Enumerated(EnumType.STRING)
    private JobTitle jobTitle;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @PrePersist     // ΚΑΘΕ ΦΟΡΑ ΠΟΥ ΘΑ ΠΑΕΙ ΝΑ ΚΑΝΕΙ SAVE ΘΑ ΕΛΕΓΧΕΙ ΤΟ UUID ΑΝ ΕΙΝΑΙ NULL. ΑΝ ΕΙΝΑΙ ΘΑ ΤΟΥ ΔΙΝΕΙ ΕΝΑ
    public void initializeUUID(){
        if (uuid == null) uuid = UUID.randomUUID().toString();
    }
}
