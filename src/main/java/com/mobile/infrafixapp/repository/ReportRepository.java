package com.mobile.infrafixapp.repository;

import com.mobile.infrafixapp.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Report> findAllByOrderByCreatedAtDesc();
}
