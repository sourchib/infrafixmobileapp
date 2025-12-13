package com.mobile.infrafixapp.repository;

import com.mobile.infrafixapp.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReportStatusRepository extends JpaRepository<ReportStatus, Integer> {
    Optional<ReportStatus> findByName(String name);
}
