package ru.effectivemobile.taskmanagement.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.effectivemobile.taskmanagement.model.Priority;
import ru.effectivemobile.taskmanagement.model.enums.PriorityCode;
import ru.effectivemobile.taskmanagement.repository.PriorityRepository;

@Component
public class PriorityInitializer {

    private final PriorityRepository priorityRepository;

    public PriorityInitializer(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @PostConstruct
    private void init() {
        for (PriorityCode code: PriorityCode.values()) {
            priorityRepository.findByCode(code.name()).orElseGet(() -> {
                Priority priority = new Priority();
                priority.setCode(code.name());
                return priorityRepository.save(priority);
            });
        }
    }
}
