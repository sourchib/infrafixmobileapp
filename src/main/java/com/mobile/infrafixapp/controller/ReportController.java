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
    public ResponseEntity<ReportResponse> createReport(
            @RequestPart("data") ReportRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(reportService.createReport(request, images));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReportResponse>> getMyReports() {
        return ResponseEntity.ok(reportService.getMyReports());
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() { // For Admin/Tech
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateStatus(
            @PathVariable Integer id,
            @RequestBody ReportStatusUpdateRequest request) { // For Admin
        return ResponseEntity.ok(reportService.updateReportStatus(id, request));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ReportCategory>> getCategories() {
        return ResponseEntity.ok(reportService.getCategories());
    }
}
