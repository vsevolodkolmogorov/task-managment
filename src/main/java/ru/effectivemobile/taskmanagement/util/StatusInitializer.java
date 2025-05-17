package ru.effectivemobile.taskmanagement.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.effectivemobile.taskmanagement.model.Status;
import ru.effectivemobile.taskmanagement.model.enums.StatusCode;
import ru.effectivemobile.taskmanagement.repository.StatusRepository;

@Component
public class StatusInitializer {

    private final StatusRepository statusRepository;

    public StatusInitializer(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @PostConstruct
    public void init() {
        for (StatusCode code : StatusCode.values()) {
            statusRepository.findByCode(code.name()).orElseGet(() -> {
                Status status = new Status();
                status.setCode(code.name());
                return statusRepository.save(status);
            });
        }
    }
}
