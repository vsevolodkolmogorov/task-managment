package ru.effectivemobile.taskmanagement.model;

/**
 * Enum representing the roles of a user in the system.
 * <p>
 * Defines the different user roles that dictate the level of access and permissions within the application.
 * </p>
 */
public enum Role {

    /**
     * Regular user role - typically has limited access and can perform basic actions.
     */
    USER,

    /**
     * Admin role - has elevated permissions, including the ability to manage users and tasks.
     */
    ADMIN
}
