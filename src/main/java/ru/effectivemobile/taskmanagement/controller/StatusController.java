package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effectivemobile.taskmanagement.dto.RoleDTO;
import ru.effectivemobile.taskmanagement.dto.StatusDTO;
import ru.effectivemobile.taskmanagement.repository.StatusRepository;

import java.util.List;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/status")
@Tag(name = "Status", description = "Endpoints for get status of tasks")
@RequiredArgsConstructor
public class StatusController {

    private final StatusRepository statusRepository;

    /**
     * Get a status of the tasks in the system.
     *
     * @return a response with a list of status.
     */
    @GetMapping
    public List<StatusDTO> getAllStatus() {
        return statusRepository.findAll()
                .stream()
                .map(status -> new StatusDTO(status.getId(), status.getCode()))
                .toList();
    }
}
