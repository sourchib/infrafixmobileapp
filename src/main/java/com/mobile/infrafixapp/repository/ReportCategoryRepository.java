package com.mobile.infrafixapp.repository;

import com.mobile.infrafixapp.model.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Integer> {
    Optional<ReportCategory> findByName(String name);
}
