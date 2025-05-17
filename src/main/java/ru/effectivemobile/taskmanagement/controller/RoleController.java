package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effectivemobile.taskmanagement.dto.RoleDTO;
import ru.effectivemobile.taskmanagement.repository.RoleRepository;

import java.util.List;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints for get roles of users")
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleDTO(role.getId(), role.getCode()))
                .toList();
    }
}
