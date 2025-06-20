package com.ducnt.pcronosbe.service;

import com.ducnt.pcronosbe.dto.common.ApiResponse;
import com.ducnt.pcronosbe.dto.common.PhongVuBodyRequest;
import com.ducnt.pcronosbe.dto.common.PhongVuDataResponse;
import com.ducnt.pcronosbe.dto.product.PhongVuProductDetailResponse;
import com.ducnt.pcronosbe.dto.product.ProductDTO;
import com.ducnt.pcronosbe.dto.product.ProductQueue;
import com.ducnt.pcronosbe.exception.CustomException;
import com.ducnt.pcronosbe.exception.ErrorResponse;
import com.ducnt.pcronosbe.model.Attribute;
import com.ducnt.pcronosbe.model.Image;
import com.ducnt.pcronosbe.model.Product;
import com.ducnt.pcronosbe.model.UnitOfMeasure;
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
import org.modelmapper.TypeToken;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ModelMapper modelMapper;
    OkHttpClient okHttpClient;
    ProductRepository productRepository;
    ObjectMapper objectMapper;

    @Transactional
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void crawlPhongVu() {
        String url = "https://discovery.tekoapis.com/api/v2/search-skus-v2";
        int maxRetries = 3;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                PhongVuBodyRequest pvRequest = new PhongVuBodyRequest();
                for (int i = pvRequest.getPage(); i <= pvRequest.getLimit(); i++) {
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
                        if(response.body() == null) {
                            throw new Exception("Response body is null");
                        }
                        String responseBody = response.body().string();
                        ApiResponse apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
                        if(apiResponse == null || apiResponse.getData() == null) {
                            return;
                        }
                        PhongVuDataResponse phongVuDataResponse = modelMapper
                                .map(apiResponse.getData(), PhongVuDataResponse.class);
                        phongVuDataResponse.onCreateProductQueue();

                        List<Product> productList = phongVuDataResponse
                                .getProducts()
                                .stream()
                                .map(p -> Product.builder()
                                        .name(p.getName())
                                        .sku(p.getSku())
                                        .avatar(p.getImageUrl())
                                        .shortDescription(normalizeToDescription(p.getShortDescription()))
                                        .bodyHtml(p.getShortDescription())
                                        .vendor(p.getBrandName())
                                        .canonical(p.getCanonical())
                                        .price(parsePrice(p.getLatestPrice()))
                                        .build())
                                .collect(Collectors.toList());

                        productRepository.saveAll(productList);
                    } catch (IOException e) {
                        log.error("Attempt {} - Network error: {}", attempt, e.getMessage(), e);
                    }
                    pvRequest.setLimit(pvRequest.getLimit() - 10);
                    pvRequest.setPage(pvRequest.getPage() + 1);
                }
            } catch (Exception e) {
                log.error("Attempt {} - Unexpected error: {}", attempt, e.getMessage(), e);
                return;
            }
        }
    }

    @Async
    public void crawlProductDetail() {
        StringBuilder baseUrl = new StringBuilder("https://discovery.tekoapis.com/api/v1/product?terminalCode=phongvu&sku=");
        String rollBack = ProductQueue.skuQueue.peek();
        while(!ProductQueue.skuQueue.isEmpty()) {
            String sku = ProductQueue.skuQueue.poll();
            Request request = new Request.Builder()
                    .url(baseUrl + sku)
                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                    .get()
                    .build();
            try(Response response = okHttpClient.newCall(request).execute()) {
                if(!response.isSuccessful()) {
                    log.error("HTTP error: {} - {}", response.code(), response.message());
                    continue;
                }
                if(response.body() == null) {
                    throw new IOException("Response body is null");
                }
                String responseBody = response.body().string();
                ApiResponse apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);
                if(apiResponse == null || apiResponse.getResult() == null) {
                    throw new IOException("Response body is null");
                }
                PhongVuProductDetailResponse productDetail = PhongVuProductDetailResponse.onCreation(apiResponse.getResult());
                if(productDetail == null) {
                    continue;
                }
                List<Product> productList = productRepository.findBySku(sku);
                Product product = productList.get(0);
                product.setDescription(productDetail.getDescription());
                product.setAttributes(modelMapper.map(productDetail.getAttributeDTOS(),
                        new TypeToken<List<Attribute>>(){}.getType()));
                product.setImages(modelMapper.map(productDetail.getImageDTOS(),
                        new TypeToken<List<Image>>(){}.getType()));
                product.setUnitOfMeasures(modelMapper.map(productDetail.getUnitOfMeasuresDTOS(),
                        new TypeToken<List<UnitOfMeasure>>(){}.getType()));
                productRepository.save(product);

            } catch (IOException e) {
                ProductQueue.skuQueue.add(rollBack);
                throw new CustomException(ErrorResponse.IO_FILE_INVALID);
            } catch (Exception e) {
                ProductQueue.skuQueue.add(rollBack);
                throw new CustomException(ErrorResponse.INTERNAL_SERVER);
            }
        }
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

    public List<ProductDTO> getProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream()
                .map((element) -> modelMapper.map(element, ProductDTO.class))
                .toList();
        return productDTOS;
    }
}
