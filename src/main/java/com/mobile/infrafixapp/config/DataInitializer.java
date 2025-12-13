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

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound("Citizen");
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("Technician");

        createStatusIfNotFound("MENUNGGU");
        createStatusIfNotFound("DIPROSES");
        createStatusIfNotFound("SELESAI");
        createStatusIfNotFound("DITOLAK");

        createCategoryIfNotFound("INFRASTRUKTUR");
        createCategoryIfNotFound("KEAMANAN");
        createCategoryIfNotFound("KEBERSIHAN");
        createCategoryIfNotFound("LAINNYA");
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .build();
            roleRepository.save(role);
        }
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
}
