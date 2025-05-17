package ru.effectivemobile.taskmanagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.effectivemobile.taskmanagement.model.enums.StatusCode;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a task in the system.
 * <p>
 * A task consists of various attributes such as title, description, priority, status, and
 * associations with the author and assignee (users).
 * </p>
 */
@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    /**
     * Unique identifier for the task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the task.
     * Provides a short description of the task.
     */
    private String title;

    /**
     * Detailed description of the task.
     * Specifies the requirements, steps, or additional information about the task.
     */
    private String description;

    /**
     * Priority level of the task.
     * Determines the urgency of the task (e.g., LOW, MEDIUM, HIGH).
     */
    @Enumerated(EnumType.STRING)
    private Priority priority;


    @ManyToOne
    @JoinColumn(name = "status_id")
    @JsonManagedReference
    private Status status;

    /**
     * The author (creator) of the task.
     * Represents the user who created the task.
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonManagedReference
    private User author;

    /**
     * The assignee of the task.
     * Represents the user to whom the task is assigned.
     */
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @JsonManagedReference
    private User assignee;

    /**
     * The creation timestamp of the task.
     * Automatically populated when the task is created.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * The update timestamp of the task.
     * Automatically populated when the task is created.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;

}
