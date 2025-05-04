package com.ducnt.pcronosbe.service.interfaces;

import com.ducnt.pcronosbe.dto.authentication.*;
import com.ducnt.pcronosbe.model.Account;
import com.nimbusds.jose.JOSEException;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

public interface IAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest request);

    String generateAccessToken(Account account);

    String generateRefreshToken(Account account);

    @Transactional
    boolean register(RegisterRequest request);

    @Transactional
    boolean verifyAccountByOtp(VerifyOtpRequest request);

    void logout(LogoutRequest request);

    boolean introspect(String token) throws JOSEException, ParseException;
}
