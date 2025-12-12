package com.mobile.infrafixapp.dto.request;

import com.mobile.infrafixapp.model.ReportCategory;
import com.mobile.infrafixapp.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportStatusUpdateRequest {
    private ReportStatus status;
    private ReportCategory category;
}
