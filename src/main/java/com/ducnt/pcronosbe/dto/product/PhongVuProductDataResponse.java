package com.ducnt.pcronosbe.dto.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhongVuProductDataResponse {
    String name;
    String imageUrl;
    String canonical;
    String brandName;
    String shortDescription;
    String latestPrice;
}
