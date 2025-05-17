package ru.effectivemobile.taskmanagement.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.enums.RoleCode;
import ru.effectivemobile.taskmanagement.repository.RoleRepository;

@Component
public class RoleInitializer {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        for (RoleCode code : RoleCode.values()) {
            roleRepository.findByCode(code.name()).orElseGet(() -> {
                Role role = new Role();
                role.setCode(code.name());
                return roleRepository.save(role);
            });
        }
    }
}

