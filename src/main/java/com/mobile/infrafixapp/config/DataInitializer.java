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
    private final com.mobile.infrafixapp.repository.ReportStatusRepository reportStatusRepository;
    private final com.mobile.infrafixapp.repository.ReportCategoryRepository reportCategoryRepository;
    private final com.mobile.infrafixapp.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("Citizen");
        Role adminRole = createRoleIfNotFound("Admin");
        Role techRole = createRoleIfNotFound("Technician");

        createStatusIfNotFound("MENUNGGU");
        createStatusIfNotFound("DIPROSES");
        createStatusIfNotFound("SELESAI");
        createStatusIfNotFound("DITOLAK");

        createCategoryIfNotFound("INFRASTRUKTUR");
        createCategoryIfNotFound("KEAMANAN");
        createCategoryIfNotFound("KEBERSIHAN");
        createCategoryIfNotFound("LAINNYA");

        createUserIfNotFound("admin@infrafix.com", "Admin InfraFix", "password", adminRole);
        createUserIfNotFound("technician@infrafix.com", "Technician InfraFix", "password", techRole);
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder()
                    .name(name)
                    .build();
            return roleRepository.save(role);
        });
    }

    private void createStatusIfNotFound(String name) {
        if (reportStatusRepository.findByName(name).isEmpty()) {
            com.mobile.infrafixapp.model.ReportStatus status = com.mobile.infrafixapp.model.ReportStatus.builder()
                    .name(name)
                    .build();
            reportStatusRepository.save(status);
        }
    }

    private void createCategoryIfNotFound(String name) {
        if (reportCategoryRepository.findByName(name).isEmpty()) {
            com.mobile.infrafixapp.model.ReportCategory category = com.mobile.infrafixapp.model.ReportCategory.builder()
                    .name(name)
                    .build();
            reportCategoryRepository.save(category);
        }
    }

    private void createUserIfNotFound(String email, String fullName, String password, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            com.mobile.infrafixapp.model.User user = com.mobile.infrafixapp.model.User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build();
            userRepository.save(user);
        }
    }
}
