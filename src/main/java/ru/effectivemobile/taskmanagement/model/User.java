package ru.effectivemobile.taskmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entity representing a user in the system.
 * <p>
 * Implements Spring Security's {@link UserDetails} interface to provide user authentication and authorization functionality.
 * </p>
 */

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The full name of the user, used as the info about user.
     */
    private String fullName;

    /**
     * The email of the user, used as the username for authentication.
     */
    private String email;

    /**
     * The password of the user, used for authentication.
     */
    private String password;

    /**
     * The role assigned to the user (e.g., USER or ADMIN).
     * Determines the user's access level and permissions within the system.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * List of tasks authored by the user.
     * This represents the tasks created by the user.
     */
    @OneToMany(mappedBy = "author")
    @JsonBackReference
    private List<Task> authoredTasks;

    /**
     * List of tasks assigned to the user.
     * This represents the tasks that are assigned to the user for completion.
     */
    @OneToMany(mappedBy = "assignee")
    @JsonBackReference
    private List<Task> assignedTasks;

    /**
     * Retrieves the authorities (roles) granted to the user.
     * This method is required for Spring Security to determine the user's roles and permissions.
     *
     * @return a collection of granted authorities (roles)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    /**
     * Retrieves the password of the user.
     * This method is required for Spring Security's authentication process.
     *
     * @return the password of the user
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Retrieves the username (email) of the user.
     * This method is required for Spring Security's authentication process.
     *
     * @return the email of the user
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Checks if the user's account is non-expired.
     * By default, returns true (this can be overridden for more complex logic).
     *
     * @return true if the account is not expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Checks if the user's account is non-locked.
     * By default, returns true (this can be overridden for more complex logic).
     *
     * @return true if the account is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Checks if the user's credentials are non-expired.
     * By default, returns true (this can be overridden for more complex logic).
     *
     * @return true if the credentials are not expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Checks if the user is enabled.
     * By default, returns true (this can be overridden for more complex logic).
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
