package com.mobile.infrafixapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private boolean termsAccepted;
    private boolean dataUsageAccepted;
}
