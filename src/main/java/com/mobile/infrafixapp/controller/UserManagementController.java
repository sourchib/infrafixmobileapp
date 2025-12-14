package com.mobile.infrafixapp.controller;

import com.mobile.infrafixapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mobile.infrafixapp.dto.request.RegisterRequest;
import com.mobile.infrafixapp.dto.response.AuthenticationResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/technician")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<AuthenticationResponse> createTechnician(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createTechnician(request));
    }
}
