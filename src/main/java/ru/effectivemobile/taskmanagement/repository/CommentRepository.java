package ru.effectivemobile.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.effectivemobile.taskmanagement.model.Comment;
import ru.effectivemobile.taskmanagement.model.Task;
import ru.effectivemobile.taskmanagement.model.User;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByTaskId(long taskId);
}
