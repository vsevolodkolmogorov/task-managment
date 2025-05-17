package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effectivemobile.taskmanagement.dto.AuthResponseDTO;
import ru.effectivemobile.taskmanagement.dto.LoginRequestDTO;
import ru.effectivemobile.taskmanagement.dto.RegisterRequestDTO;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.repository.RoleRepository;
import ru.effectivemobile.taskmanagement.repository.UserRepository;
import ru.effectivemobile.taskmanagement.service.CommentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private User USER;
    private String JWT_TOKEN;
    private Role USER_ROLE;


    @BeforeEach()
    void SetUp() {
        USER = User.builder().email("test@gmail.com").id(1L).password("testPassword").role(USER_ROLE).build();
        USER_ROLE = Role.builder().id(1L).code("USER").build();
        JWT_TOKEN = "token";
    }

    @Test
    void register() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("testPassword");
        when(userRepository.save(any(User.class))).thenReturn(USER);
        when(roleRepository.findByCode(any(String.class))).thenReturn(Optional.ofNullable(USER_ROLE));
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);

        AuthResponseDTO actual = authService.register(new RegisterRequestDTO("John Duo", "test@gmail.com", "testPassword"));
        Assertions.assertEquals(actual.getToken(), JWT_TOKEN);
        Assertions.assertEquals(actual.getUser().getUsername(), USER.getUsername());
        Assertions.assertEquals(actual.getUser().getPassword(), USER.getPassword());

    }

    @Test
    void login() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@gmail.com", "testPassword");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userRepository.findByEmail(USER.getEmail())).thenReturn(Optional.ofNullable(USER));
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);

        AuthResponseDTO actual = authService.login(new LoginRequestDTO("test@gmail.com", "testPassword"));
        Assertions.assertEquals(actual.getToken(), JWT_TOKEN);
        Assertions.assertEquals(actual.getUser().getUsername(), USER.getUsername());
        Assertions.assertEquals(actual.getUser().getPassword(), USER.getPassword());
    }

    @Test
    void getCurrentUser() {
    }
}