package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.request.ReportRequest;
import com.mobile.infrafixapp.dto.request.ReportStatusUpdateRequest;
import com.mobile.infrafixapp.dto.response.ReportResponse;
import com.mobile.infrafixapp.model.Report;
import com.mobile.infrafixapp.model.ReportCategory;
import com.mobile.infrafixapp.model.ReportStatus;
import com.mobile.infrafixapp.model.User;
import com.mobile.infrafixapp.repository.ReportCategoryRepository;
import com.mobile.infrafixapp.repository.ReportRepository;
import com.mobile.infrafixapp.repository.ReportStatusRepository;
import com.mobile.infrafixapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import org.apache.commons.codec.digest.DigestUtils;
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
    private final ReportStatusRepository reportStatusRepository;
    private final ReportCategoryRepository reportCategoryRepository;
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

        if (request.getKtpNumber() != null && !request.getKtpNumber().isEmpty()) {
            user.setKtpNumber(request.getKtpNumber());
            userRepository.save(user); // Update user KTP
        }

        ReportStatus status = reportStatusRepository.findByName("MENUNGGU")
                .orElseThrow(() -> new RuntimeException("Status MENUNGGU not found"));

        String categoryName = extractCategoryName(request.getCategory());

        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new RuntimeException("Category is required");
        }

        ReportCategory category = reportCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category '" + categoryName + "' not found"));

        Report report = Report.builder()
                .title(request.getTitle() != null ? request.getTitle() : "Laporan dari " + user.getFullName())
                .description(request.getDescription())
                .category(category)
                .categoryName(category.getName())
                .status(status)
                .statusName(status.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDataValid(request.isDataValid())
                .incidentDate(request.getIncidentDate() != null ? request.getIncidentDate() : LocalDate.now())
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
            ReportStatus s = reportStatusRepository.findByName(request.getStatus())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            report.setStatus(s);
            report.setStatusName(s.getName());
        }
        if (request.getCategory() != null) {
            String categoryName = extractCategoryName(request.getCategory());

            ReportCategory c = reportCategoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Category " + categoryName + " not found"));
            report.setCategory(c);
            report.setCategoryName(c.getName());
        }

        return mapToResponse(reportRepository.save(report));
    }

    public ReportResponse updateReportStatusById(Integer id, Integer statusId) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        ReportStatus status = reportStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        report.setStatus(status);
        report.setStatusName(status.getName());

        return mapToResponse(reportRepository.save(report));
    }

    public List<ReportCategory> getCategories() {
        return reportCategoryRepository.findAll();
    }

    public ReportCategory addCategory(String name) {
        if (reportCategoryRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Category already exists");
        }
        return reportCategoryRepository.save(ReportCategory.builder().name(name).build());
    }

    private String storeFile(MultipartFile file) {
        try {
            // Calculate Hash
            String checksum;
            try (java.io.InputStream is = file.getInputStream()) {
                checksum = DigestUtils.sha256Hex(is);
            }

            // Save new file using hash as filename for simple deduplication on disk
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int i = originalFileName.lastIndexOf('.');
            if (i > 0) {
                extension = originalFileName.substring(i);
            }
            String fileName = checksum + extension;

            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            // Overwrite existing file seamlessly (deduplication)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    private String extractCategoryName(Object categoryObj) {
        if (categoryObj instanceof String) {
            return (String) categoryObj;
        } else if (categoryObj instanceof java.util.Map) {
            return (String) ((java.util.Map<?, ?>) categoryObj).get("name");
        }
        return null; // Or handle as error/default
    }

    private ReportResponse mapToResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .category(report.getCategory() != null ? report.getCategory().getName()
                        : (report.getCategoryName() != null ? report.getCategoryName() : "Uncategorized"))
                .status(report.getStatus() != null ? report.getStatus().getName()
                        : (report.getStatusName() != null ? report.getStatusName() : "Unknown"))
                .address(report.getAddress())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .incidentDate(report.getIncidentDate())
                .image1(report.getImage1())
                .image2(report.getImage2())
                .image3(report.getImage3())
                .reporterName(report.getUser() != null ? report.getUser().getFullName() : "Anonymous")
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
