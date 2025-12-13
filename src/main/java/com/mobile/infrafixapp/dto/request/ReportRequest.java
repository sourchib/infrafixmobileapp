package com.mobile.infrafixapp.dto.request;

import com.mobile.infrafixapp.model.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    private String fullName;
    private String ktpNumber;
    private java.time.LocalDate incidentDate;
    private String category;
    private String description;
    private String title; // Keeping it as it might be needed or derived
    private String address;
    private Double latitude;
    private Double longitude;
    private boolean isDataValid;
}
