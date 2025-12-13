package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.request.AuthenticationRequest;
import com.mobile.infrafixapp.dto.request.RegisterRequest;
import com.mobile.infrafixapp.dto.response.AuthenticationResponse;
import com.mobile.infrafixapp.model.User;
import com.mobile.infrafixapp.repository.UserRepository;
import com.mobile.infrafixapp.security.JwtService;
import com.mobile.infrafixapp.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

        private final UserRepository repository;
        private final com.mobile.infrafixapp.repository.RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserValidator userValidator;

        public AuthenticationResponse register(RegisterRequest request) {
                userValidator.validate(request);

                var role = roleRepository.findByName("Citizen")
                                .orElseThrow(() -> new RuntimeException("Role Citizen not initialized"));

                var user = User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .address(request.getAddress())
                                .phoneNumber(request.getPhoneNumber())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .postalCode(request.getPostalCode())
                                .latitude(request.getLatitude())
                                .longitude(request.getLongitude())
                                .termsAccepted(request.isTermsAccepted())
                                .dataUsageAccepted(request.isDataUsageAccepted())
                                .role(role)
                                .build();

                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .status("success")
                                .message("Registrasi berhasil.")
                                .token(jwtToken)
                                .user(AuthenticationResponse.UserInfo.builder()
                                                .id_pengguna(user.getId())
                                                .fullName(user.getFullName())
                                                .email(user.getEmail())
                                                .role(user.getRole().getName())
                                                .build())
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .status("success")
                                .message("Login berhasil.")
                                .token(jwtToken)
                                .user(AuthenticationResponse.UserInfo.builder()
                                                .id_pengguna(user.getId())
                                                .fullName(user.getFullName())
                                                .email(user.getEmail())
                                                .role(user.getRole().getName())
                                                .build())
                                .build();
        }

        public void deleteUser(Integer id) {
                repository.deleteById(id);
        }
}
