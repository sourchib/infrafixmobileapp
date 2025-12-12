package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.request.ReportRequest;
import com.mobile.infrafixapp.dto.request.ReportStatusUpdateRequest;
import com.mobile.infrafixapp.dto.response.ReportResponse;
import com.mobile.infrafixapp.model.Report;
import com.mobile.infrafixapp.model.ReportCategory;
import com.mobile.infrafixapp.model.ReportStatus;
import com.mobile.infrafixapp.model.User;
import com.mobile.infrafixapp.repository.ReportRepository;
import com.mobile.infrafixapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public ReportResponse createReport(ReportRequest request, List<MultipartFile> images) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Simple directory creation
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }

        String[] storedImages = new String[3];
        if (images != null) {
            for (int i = 0; i < Math.min(images.size(), 3); i++) {
                storedImages[i] = storeFile(images.get(i));
            }
        }

        Report report = Report.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(ReportStatus.MENUNGGU) // Default status
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .incidentDate(LocalDate.now())
                .user(user)
                .image1(storedImages[0])
                .image2(storedImages[1])
                .image3(storedImages[2])
                .build();

        Report savedReport = reportRepository.save(report);
        return mapToResponse(savedReport);
    }

    public List<ReportResponse> getMyReports() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByEmail(email).orElseThrow();

        return reportRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReportResponse updateReportStatus(Integer id, ReportStatusUpdateRequest request) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));

        if (request.getStatus() != null) {
            report.setStatus(request.getStatus());
        }
        if (request.getCategory() != null) {
            report.setCategory(request.getCategory());
        }

        return mapToResponse(reportRepository.save(report));
    }

    public List<ReportCategory> getCategories() {
        return Arrays.asList(ReportCategory.values());
    }

    private String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    private ReportResponse mapToResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .category(report.getCategory())
                .status(report.getStatus())
                .address(report.getAddress())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .incidentDate(report.getIncidentDate())
                .image1(report.getImage1())
                .image2(report.getImage2())
                .image3(report.getImage3())
                .reporterName(report.getUser().getFullName())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
