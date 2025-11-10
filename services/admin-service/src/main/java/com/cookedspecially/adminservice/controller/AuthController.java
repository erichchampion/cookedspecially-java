package com.cookedspecially.adminservice.controller;

import com.cookedspecially.adminservice.domain.OTPType;
import com.cookedspecially.adminservice.dto.auth.*;
import com.cookedspecially.adminservice.service.AuthenticationService;
import com.cookedspecially.adminservice.service.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final OTPService otpService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "User login with username/email and password")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        LoginResponseDTO response = authenticationService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "User logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String sessionToken = authHeader.replace("Bearer ", "");
        authenticationService.logout(sessionToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/generate")
    @Operation(summary = "Generate OTP", description = "Generate OTP for user")
    public ResponseEntity<String> generateOTP(@Valid @RequestBody OTPRequestDTO request) {
        String otp = otpService.generateOTP(request.getUserId(), request.getType());
        return ResponseEntity.ok("OTP generated successfully");
    }

    @PostMapping("/otp/validate")
    @Operation(summary = "Validate OTP", description = "Validate OTP code")
    public ResponseEntity<Boolean> validateOTP(@Valid @RequestBody OTPValidateDTO request) {
        boolean valid = otpService.validateOTP(request.getUserId(), request.getCode(), OTPType.MFA);
        return ResponseEntity.ok(valid);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password", description = "Reset user password with OTP")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        authenticationService.resetPassword(request.getUserId(), request.getOtpCode(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
