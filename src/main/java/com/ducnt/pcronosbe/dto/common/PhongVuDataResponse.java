package com.ducnt.pcronosbe.dto.common;

import com.ducnt.pcronosbe.dto.product.PhongVuProductDataResponse;
import com.ducnt.pcronosbe.dto.product.ProductQueue;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class  PhongVuDataResponse {
    List<PhongVuProductDataResponse> products;
    Object filter;
    int page;
    int pageSize;
    int total;

    public void onCreateProductQueue() {
        ProductQueue productQueue = new ProductQueue();
        List<String> Skus = this.products.stream().map(PhongVuProductDataResponse::getSku).toList();
        ProductQueue.addSku(Skus);
    }
}
