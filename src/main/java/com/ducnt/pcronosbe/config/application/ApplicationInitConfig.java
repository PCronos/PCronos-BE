package com.ducnt.pcronosbe.config.application;

import com.ducnt.pcronosbe.dto.common.PhongVuBodyRequest;
import com.ducnt.pcronosbe.model.Account;
import com.ducnt.pcronosbe.repository.AccountRepository;
import com.ducnt.pcronosbe.repository.ProductRepository;
import com.ducnt.pcronosbe.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableAsync
@EnableScheduling
public class ApplicationInitConfig {
    @Value("${admin-email}")
    @NonFinal
    protected String adminEmail;
    @Value("${admin-password}")
    @NonFinal
    protected String adminPassword;
    RedisConnectionFactory redisConnectionFactory;
    PasswordEncoder passwordEncoder;
    ProductService productService;

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository,
                                        ProductRepository productRepository) {
        return args -> {
            redisConnectionFactory.getConnection().serverCommands().flushAll();

            if(accountRepository.findByEmail(adminEmail).isEmpty()) {
                Account account = Account
                        .builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .build();
                accountRepository.save(account);
            }

            productRepository.deleteAll();

            productService.crawlPhongVu();
        };
    }

    @Scheduled(fixedRate = 30000)
    public void scheduledCrawlDetails() {
        productService.crawlProductDetail();
    }
}
