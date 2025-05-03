package ru.effectivemobile.taskmanagement.model;

/**
 * Enum representing the priority levels of a task.
 * <p>
 * Defines the importance of a task in terms of priority. The priority level determines
 * the urgency or importance of the task and can be used for task scheduling, resource allocation, etc.
 * </p>
 */
public enum Priority {

    /**
     * Low priority - tasks that are not urgent or can be delayed.
     */
    LOW,

    /**
     * Medium priority - tasks that are of moderate importance or urgency.
     */
    MEDIUM,

    /**
     * High priority - tasks that are urgent and should be handled as soon as possible.
     */
    HIGH
}
