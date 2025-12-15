package com.mobile.infrafixapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String postalCode;
    private Double latitude;
    private Double longitude;
}
