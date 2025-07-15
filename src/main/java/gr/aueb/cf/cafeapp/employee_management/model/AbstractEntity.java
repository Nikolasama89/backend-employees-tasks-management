package gr.aueb.cf.cafeapp.employee_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {

    // 1. λογω convention MySql, 2. δεν θελουμε να ειναι null το createdAt, 3. να μην αλλαζει σε καθε update.
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)   //επεξηγηση στο comment απο πανω
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
