package com.mobile.infrafixapp.controller;

import com.mobile.infrafixapp.dto.request.ReportRequest;
import com.mobile.infrafixapp.dto.request.ReportStatusUpdateRequest;
import com.mobile.infrafixapp.dto.response.ReportResponse;
import com.mobile.infrafixapp.model.ReportCategory;
import com.mobile.infrafixapp.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Citizen')")
    public ResponseEntity<ReportResponse> createReport(
            @RequestPart("data") ReportRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(reportService.createReport(request, images));
    }

    @GetMapping("/my")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Citizen')")
    public ResponseEntity<List<ReportResponse>> getMyReports() {
        return ResponseEntity.ok(reportService.getMyReports());
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('Technician', 'Admin')")
    public ResponseEntity<List<ReportResponse>> getAllReports() { // For Admin/Tech
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PatchMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('Technician', 'Admin')")
    public ResponseEntity<ReportResponse> updateStatus(
            @PathVariable Integer id,
            @RequestBody ReportStatusUpdateRequest request) { // For Admin/Tech
        return ResponseEntity.ok(reportService.updateReportStatus(id, request));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ReportCategory>> getCategories() {
        return ResponseEntity.ok(reportService.getCategories());
    }

    @PostMapping("/categories")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ReportCategory> createCategory(
            @RequestBody com.mobile.infrafixapp.dto.request.CategoryRequest request) {
        return ResponseEntity.ok(reportService.addCategory(request.getName()));
    }
}
