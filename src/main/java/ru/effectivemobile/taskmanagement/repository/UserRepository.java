package ru.effectivemobile.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.effectivemobile.taskmanagement.model.User;

import java.util.Optional;

/**
 * Repository for performing CRUD operations on {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to provide basic CRUD functionality,
 * along with custom queries for managing user-related data.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * <p>
     * This method is used to retrieve a user based on their email,
     * which is unique in the system.
     * </p>
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the user if found, or an empty {@link Optional} if not found
     */
    Optional<User> findByEmail(String email);
}
