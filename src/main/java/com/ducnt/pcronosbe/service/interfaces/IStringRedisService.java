package com.ducnt.pcronosbe.service.interfaces;

public interface IStringRedisService {
    void saveOtp(String email, String otp, long duration);

    String getOtp(String email);

    void deleteOtp(String email);

    void saveInvalidToken(String invalidToken, String value, long duration);

    String getInvalidToken(String invalidToken);
}
