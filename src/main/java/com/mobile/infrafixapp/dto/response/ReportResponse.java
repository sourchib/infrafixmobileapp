package com.mobile.infrafixapp.dto.response;

import com.mobile.infrafixapp.model.ReportCategory;
import com.mobile.infrafixapp.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private Integer id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDate incidentDate;
    private String image1;
    private String image2;
    private String image3;
    private String reporterName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
