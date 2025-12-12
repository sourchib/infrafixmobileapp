package com.mobile.infrafixapp.dto.request;

import com.mobile.infrafixapp.model.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    private String title;
    private String description;
    private ReportCategory category;
    private String address;
    private Double latitude;
    private Double longitude;

    // Images handled separately in controller but logic might use this DTO if not
    // using @RequestPart
}
