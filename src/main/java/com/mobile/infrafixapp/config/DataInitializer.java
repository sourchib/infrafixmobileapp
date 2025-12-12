package com.mobile.infrafixapp.config;

import com.mobile.infrafixapp.model.Role;
import com.mobile.infrafixapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("Citizen");
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("Technician");
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .build();
            roleRepository.save(role);
        }
    }
}
