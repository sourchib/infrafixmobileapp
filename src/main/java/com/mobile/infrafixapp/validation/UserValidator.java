package com.mobile.infrafixapp.validation;

import com.mobile.infrafixapp.dto.request.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validate(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (!request.isTermsAccepted()) {
            throw new IllegalArgumentException("You must agree to the Terms & Conditions");
        }
        if (!request.isDataUsageAccepted()) {
            throw new IllegalArgumentException("You must allow your data to be used");
        }
    }
}
