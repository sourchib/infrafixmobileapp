package com.mobile.infrafixapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String time;
    private boolean read;
    private String type;
}
