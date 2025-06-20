package com.ducnt.pcronosbe.controller;

import com.ducnt.pcronosbe.dto.common.ApiResponse;
import com.ducnt.pcronosbe.dto.product.ProductDTO;
import com.ducnt.pcronosbe.model.Product;
import com.ducnt.pcronosbe.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${my-api-base-url}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

//    @GetMapping("/crawl-phong-vu-data")
//    public ApiResponse crawlProducts() {
//        boolean flag = productService.crawlPhongVu();
//        if(!flag) {
//            return ApiResponse
//                    .builder()
//                    .message("Crawl Phong Vu Data fail")
//                    .build();
//        }
//        return ApiResponse
//                .builder()
//                .message("Crawl Phong Vu Data successful")
//                .build();
//    }

    @GetMapping("/products")
    public ApiResponse getProducts() {
        List<ProductDTO> products = productService.getProducts();
        return ApiResponse
                .builder()
                .data(products)
                .message("Products")
                .build();
    }
}
