package com.ducnt.pcronosbe.service;

import com.ducnt.pcronosbe.service.interfaces.IStringRedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class StringRedisService implements IStringRedisService {
    StringRedisTemplate redisTemplate;

    
    @Override
    public void saveOtp(String email, String otp, long duration) {
        redisTemplate.opsForValue().set(email, otp, duration, TimeUnit.MINUTES);
    }

    
    @Override
    public String getOtp(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    
    @Override
    public void deleteOtp(String email) {
        redisTemplate.delete(email);
    }

    
    @Override
    public void saveInvalidToken(String invalidToken, String value, long duration) {
        redisTemplate.opsForValue().set(invalidToken, String.valueOf(value), duration, TimeUnit.MINUTES);
    }

    
    @Override
    public String getInvalidToken(String invalidToken) {
        return redisTemplate.opsForValue().get(invalidToken);
    }
}
