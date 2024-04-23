package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Task implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private long id;

    @Column
    @NotBlank
    private String name;

    @Column
    private long index;

    @Column
    private String description;

    @JoinColumn(name = "task_status_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TaskStatus taskStatus;

    @JoinColumn(name = "assignee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @Column
    @CreatedDate
    private LocalDate createdAt;
}
