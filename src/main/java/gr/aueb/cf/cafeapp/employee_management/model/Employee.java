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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(unique = true)
    private String vat;

    private String firstname;
    private String lastname;

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
}
