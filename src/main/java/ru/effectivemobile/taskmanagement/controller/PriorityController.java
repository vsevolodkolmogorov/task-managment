package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effectivemobile.taskmanagement.dto.PriorityDTO;
import ru.effectivemobile.taskmanagement.repository.PriorityRepository;

import java.util.List;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/priority")
@RequiredArgsConstructor
@Tag(name = "Priorities", description = "Endpoints for get priorities of tasks")
public class PriorityController {

    private final PriorityRepository priorityRepository;

    /**
     * Get a priorities of the tasks in the system.
     *
     * @return a response with a list of priorities.
     */
    @GetMapping
    public List<PriorityDTO> getAllPriorities() {
        return priorityRepository.findAll()
                .stream()
                .map(priority -> new PriorityDTO(priority.getId(), priority.getCode()))
                .toList();
    }
}
