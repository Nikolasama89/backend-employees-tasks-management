package gr.aueb.cf.cafeapp.employee_management.model;

import gr.aueb.cf.cafeapp.employee_management.core.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "assigned_at")
    private LocalDate assignedAt;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
