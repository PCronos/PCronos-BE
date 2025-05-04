package com.ducnt.pcronosbe.controller;

import com.ducnt.pcronosbe.dto.authentication.*;
import com.ducnt.pcronosbe.dto.common.ApiResponse;
import com.ducnt.pcronosbe.service.interfaces.IAuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication API", description = "API for authentication feature")
@RequestMapping("${my-api-base-url}")
public class AuthenticationController {
    IAuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ApiResponse
                .builder()
                .data(response)
                .message("Authentication success")
                .build();
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody RegisterRequest request) {
        boolean result = authenticationService.register(request);
        return ApiResponse
                .builder()
                .data(result)
                .message("Register success")
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean result = authenticationService.verifyAccountByOtp(request);
        return ApiResponse
                .builder()
                .data(result)
                .message("Verify otp success")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.builder().build();
    }
    @PostMapping("/refresh")
    public ApiResponse refresh(@RequestBody RefreshRequest request) {
        var responseData = authenticationService.refreshToken(request);
        return ApiResponse
                .builder()
                .data(responseData)
                .build();
    }
}
