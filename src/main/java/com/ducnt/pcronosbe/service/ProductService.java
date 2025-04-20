package com.ducnt.pcronosbe.service;

import com.ducnt.pcronosbe.dto.common.ApiResponse;
import com.ducnt.pcronosbe.dto.common.PhongVuBodyRequest;
import com.ducnt.pcronosbe.dto.common.PhongVuDataResponse;
import com.ducnt.pcronosbe.model.Product;
import com.ducnt.pcronosbe.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository repository;
    ModelMapper modelMapper;
    OkHttpClient okHttpClient;

    @Transactional
    public boolean crawlPhongVu() {
        String url = "https://discovery.tekoapis.com/api/v2/search-skus-v2";
        int maxRetries = 3;
        long retryDelayMs = Duration.ofMillis(2000).toMillis();

        ObjectMapper objectMapper = new ObjectMapper();

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                PhongVuBodyRequest pvRequest = new PhongVuBodyRequest();
                String jsonBodyRequest = objectMapper.writeValueAsString(pvRequest);
                RequestBody requestBody = RequestBody.create(
                        jsonBodyRequest,
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .build();

                try(Response response = okHttpClient.newCall(request).execute()) {
                    if(!response.isSuccessful()) {
                        log.error("Attempt {} - HTTP error: {} - {}",
                                attempt, response.code(), response.message());
                        continue;
                    }
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    ApiResponse apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
                    if(apiResponse == null || apiResponse.getData() == null) {
                        return false;
                    }
                    PhongVuDataResponse phongVuDataResponse = modelMapper
                            .map(apiResponse.getData(), PhongVuDataResponse.class);

                    List<Product> productList = phongVuDataResponse
                            .getProducts()
                            .stream()
                            .map(p -> Product.builder()
                                    .name(p.getName())
                                    .image(p.getImageUrl())
                                    .description(normalizeToDescription(p.getShortDescription()))
                                    .bodyHtml(p.getShortDescription())
                                    .vendor(p.getBrandName())
                                    .canonical(p.getCanonical())
                                    .price(parsePrice(p.getLatestPrice()))
                                    .build())
                            .collect(Collectors.toList());

                    repository.saveAll(productList);
                    log.info("Successfully saved {} products", productList.size());
                    return true;
                } catch (IOException e) {
                    log.error("Attempt {} - Network error: {}", attempt, e.getMessage(), e);
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted", ie);
                        return false;
                    }
                }

            } catch (Exception e) {
                log.error("Attempt {} - Unexpected error: {}", attempt, e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    private double parsePrice(String latestPrice) {
        if(latestPrice == null || latestPrice.isEmpty()) {
            return 0;
        }
        try {
            String cleanedLatestPrice = latestPrice.replace("[^0-9.]", "");
            return Double.parseDouble(cleanedLatestPrice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String normalizeToDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }

        if (!description.contains("<ul") && !description.contains("<li") && !description.contains("<html")) {
            return description.trim();
        }
        Document doc = Jsoup.parse(description);
        Elements elements = doc.select("ul li");

        return elements.stream()
                .map(e -> {
                    String text = e.text().trim().replaceAll("\\s+", " ");
                    String[] parts = text.split(" ", 2);
                    String component = parts[0].replace(":", "");
                    String name = parts.length > 1 ? parts[1] : "";
                    return component + ": " + name;
                })
                .collect(Collectors.joining("; "));
    }

}
