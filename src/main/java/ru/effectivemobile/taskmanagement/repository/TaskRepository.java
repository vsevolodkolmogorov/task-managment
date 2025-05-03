package ru.effectivemobile.taskmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.effectivemobile.taskmanagement.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Repository for performing CRUD operations on {@link Task} entities.
 * <p>
 * Extends {@link JpaRepository} to provide basic CRUD functionality,
 * along with custom queries related to tasks' authors and assignees.
 * </p>
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Finds all tasks where the given user is either the author or the assignee.
     *
     * @param userId the ID of the user (either as the author or assignee)
     * @return a list of tasks associated with the given user, or an empty list if no tasks are found
     */
    @Query("SELECT t FROM Task t WHERE t.author.id = :userId OR t.assignee.id = :userId")
    Page<Task> findAllByAuthorAndAssignee(@Param("userId") Long userId, Pageable pageable);

    /**
     * Finds a task by its ID where the given user is both the author and the assignee of the task.
     * This ensures that the task belongs to the user in both roles.
     *
     * @param taskId the ID of the task
     * @param userId the ID of the user (who should be both the author and assignee)
     * @return an Optional containing the task if found, or an empty Optional if not found
     */
    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.author.id = :userId AND t.assignee.id = :userId")
    Optional<Task> findByIdAuthorAndAssignee(@Param("taskId") Long taskId, @Param("userId") Long userId);
}
