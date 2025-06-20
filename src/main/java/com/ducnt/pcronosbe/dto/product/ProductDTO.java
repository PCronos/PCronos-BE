package com.ducnt.pcronosbe.dto.product;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO {
    String name;
    String shortDescription;
    double price;
    String vendor;
    String avatar;
    String canonical;
    String bodyHtml;
}
