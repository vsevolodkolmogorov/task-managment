package ru.effectivemobile.taskmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.effectivemobile.taskmanagement.dto.CommentRequestDTO;
import ru.effectivemobile.taskmanagement.dto.CommentResponseDTO;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.model.enums.RoleCode;
import ru.effectivemobile.taskmanagement.repository.RoleRepository;
import ru.effectivemobile.taskmanagement.service.CommentService;
import ru.effectivemobile.taskmanagement.service.impl.JwtServiceImpl;
import ru.effectivemobile.taskmanagement.service.impl.UserDetailsServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CommentControllerTest.MockConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CommentService commentService() {
            return Mockito.mock(CommentService.class);
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

    Role userRole = Role.builder().id(1L).code("USER").build();

    User user = User.builder()
            .email("user@gmail.com")
            .password("userPassword")
            .id(1L)
            .role(userRole)
            .build();

    long taskId = 1L;
    CommentRequestDTO requestDTO = new CommentRequestDTO("Test comment");
    CommentResponseDTO responseDTO = CommentResponseDTO.builder()
            .id(1L)
            .text("Test comment")
            .taskId(taskId)
            .authorId(user.getId())
            .authorEmail("user@gmail.com")
            .build();

    @Test
    void createComment_ShouldReturn201_WhenValidRequest() throws Exception {
        given(commentService.createUserComment(eq(taskId), any(CommentRequestDTO.class))).willReturn(responseDTO);

        mockMvc.perform(post("/tasks/{taskId}/comments", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDTO)));
    }

    @Test
    void createComment_ShouldReturn400_WhenEmptyText() throws Exception {
        CommentRequestDTO requestDTO = new CommentRequestDTO("");

        mockMvc.perform(post("/tasks/{taskId}/comments", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}
