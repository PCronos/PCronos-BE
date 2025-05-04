package com.ducnt.pcronosbe.config.authentication;

import com.ducnt.pcronosbe.dto.common.ApiResponse;
import com.ducnt.pcronosbe.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorResponse unauthenticated = ErrorResponse.UNAUTHENTICATED;

        response.setStatus(unauthenticated.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper
                .writeValueAsString(ApiResponse
                        .builder()
                        .code(unauthenticated.getHttpStatusCode().value())
                        .message(unauthenticated.getMessage() + " with wrong JWT")
                        .build()));

        response.flushBuffer();
    }
}
