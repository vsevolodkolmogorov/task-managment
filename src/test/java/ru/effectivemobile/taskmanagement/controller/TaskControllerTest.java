package ru.effectivemobile.taskmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.effectivemobile.taskmanagement.dto.TaskRequestAdminDto;
import ru.effectivemobile.taskmanagement.dto.TaskRequestUserDto;
import ru.effectivemobile.taskmanagement.dto.TaskResponseDto;
import ru.effectivemobile.taskmanagement.exceptions.TaskNotFoundException;
import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.Status;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.service.TaskService;
import ru.effectivemobile.taskmanagement.service.impl.JwtServiceImpl;
import ru.effectivemobile.taskmanagement.service.impl.UserDetailsServiceImpl;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TaskControllerTest.MockConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @TestConfiguration
    static class MockConfig {
        @Bean
        public TaskService taskService() {
            return Mockito.mock(TaskService.class);
        }

        @Bean
        public CurrentUserProvider getCurrentUser() {
            return Mockito.mock(CurrentUserProvider.class);
        }

        @Bean
        public JwtServiceImpl generateToken() {
            return Mockito.mock(JwtServiceImpl.class);
        }

        @Bean
        public UserDetailsServiceImpl userDetailsService() {
            return Mockito.mock(UserDetailsServiceImpl.class);
        }
    }

    User user = User.builder()
            .email("user@gmail.com")
            .password("userPassword")
            .id(1L)
            .role(Role.USER).build();

    User admin = User.builder()
            .email("admin@gmail.com")
            .password("adminPassword")
            .id(3L)
            .role(Role.ADMIN).build();

    TaskRequestAdminDto adminDto = TaskRequestAdminDto.builder()
            .title("test admin")
            .description("description test")
            .status(Status.IN_PROGRESS)
            .priority(Priority.HIGH)
            .assigneeId(user.getId())
            .build();

    TaskRequestAdminDto invalidAdminDto = TaskRequestAdminDto.builder()
            .build();

    TaskRequestUserDto userDto = TaskRequestUserDto.builder()
            .title("test user")
            .description("description test")
            .status(Status.IN_PROGRESS)
            .priority(Priority.HIGH)
            .build();

    TaskRequestUserDto invalidUserDto = TaskRequestUserDto.builder()
            .build();

    TaskResponseDto responseUser = TaskResponseDto.builder()
            .id(1L)
            .title("test user")
            .description("description test")
            .status(Status.IN_PROGRESS)
            .priority(Priority.HIGH)
            .authorId(user.getId())
            .authorEmail(user.getEmail())
            .assigneeId(user.getId())
            .assigneeEmail(user.getEmail())
            .build();

    TaskResponseDto responseAdmin = TaskResponseDto.builder()
            .id(1L)
            .title("test admin")
            .description("description test")
            .status(Status.IN_PROGRESS)
            .priority(Priority.HIGH)
            .authorId(admin.getId())
            .authorEmail(admin.getEmail())
            .assigneeId(user.getId())
            .assigneeEmail(user.getEmail())
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<TaskResponseDto> pageUser = new PageImpl<>(List.of(responseUser), pageable, 1);
    Page<TaskResponseDto> pageAdmin = new PageImpl<>(List.of(responseAdmin), pageable, 1);

    // POST TESTS USER CREATE

    @Test
    void createTaskUser() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.createUserTask(userDto)).willReturn(responseUser);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseUser)));

    }

    @Test
    void createTaskWithInvalidData() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTaskUserNotFound() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);

        mockMvc.perform(post("/invalid-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    // POST TESTS ADMIN CREATE

    @Test
    void createTaskAdmin() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);
        given(taskService.createAdminTask(adminDto)).willReturn(responseAdmin);

        mockMvc.perform(post("/tasks/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseAdmin)));

    }

    @Test
    void createTaskAdminWithInvalidData() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);

        mockMvc.perform(post("/tasks/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAdminDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTaskAdminNotFound() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);

        mockMvc.perform(post("/invalid-url/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminDto)))
                .andExpect(status().isNotFound());
    }

    // GET

    @Test
    void getTask() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getTask(1L)).willReturn(responseUser);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseUser)));
    }

    @Test
    void getAdminTask() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);
        given(taskService.getTask(1L)).willReturn(responseAdmin);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseAdmin)));
    }

    @Test
    void getAllTasks() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getAllTasks(any(), any(), any(Pageable.class))).willReturn(pageUser);

        mockMvc.perform(get("/tasks?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(responseUser.getTitle()))
                .andExpect(jsonPath("$.content[0].description").value(responseUser.getDescription()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAllTasksWithStatusInProgress() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getAllTasks(eq(Status.IN_PROGRESS), any(), any(Pageable.class))).willReturn(pageUser);

        mockMvc.perform(get("/tasks?page=1&size=10&status=IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value(responseUser.getStatus().toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAllTasksWithPriorityHigh() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getAllTasks(any(), eq(Priority.HIGH), any(Pageable.class))).willReturn(pageUser);

        mockMvc.perform(get("/tasks?page=1&size=10&priority=HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].priority").value(responseUser.getPriority().toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAllTasksWithPriorityAndStatus() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getAllTasks(eq(Status.IN_PROGRESS), eq(Priority.HIGH), any(Pageable.class))).willReturn(pageUser);

        mockMvc.perform(get("/tasks?page=1&size=10&status=IN_PROGRESS&priority=HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value(responseUser.getStatus().toString()))
                .andExpect(jsonPath("$.content[0].priority").value(responseUser.getPriority().toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAllTasksAdmin() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);
        given(taskService.getAllTasks(any(), any(),any(Pageable.class))).willReturn(pageAdmin);

        mockMvc.perform(get("/tasks?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(responseAdmin.getTitle()))
                .andExpect(jsonPath("$.content[0].description").value(responseAdmin.getDescription()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getTaskNotFound() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getTask(999L)).willThrow(new EntityNotFoundException("Entity not founded"));

        mockMvc.perform(get("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Entity not founded"));
    }

    @Test
    void getAllTaskNotFound() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.getAllTasks(any(), any(), any(Pageable.class))).willThrow(new EntityNotFoundException("Entity not founded"));

        mockMvc.perform(get("/taskss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // UPDATE

    @Test
    void updateTaskUser() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        given(taskService.updateUserTask(1L, userDto)).willReturn(responseUser);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseUser)));
    }

    @Test
    void updateTaskUserWithInvalidData() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateTaskAdmin() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);
        given(taskService.updateAdminTask(1L, adminDto)).willReturn(responseAdmin);


        mockMvc.perform(put("/tasks/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseAdmin)));
    }

    @Test
    void updateTaskAdminWithInvalidData() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);

        mockMvc.perform(put("/tasks/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAdminDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE

    @Test
    void deleteTaskUser() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(user);
        willDoNothing().given(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_notFound_shouldReturn404() throws Exception {
        given(currentUserProvider.getCurrentUser()).willReturn(admin);

        willThrow(new TaskNotFoundException("Task with id " + 1 + " not found"))
                .given(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound());
    }
}