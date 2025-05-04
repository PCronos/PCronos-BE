package com.ducnt.pcronosbe.dto.common;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PhongVuBodyRequest {
    int terminalId = 4;
    int page = 1;
    int pageSize = 10;
    String slug = "/c/may-tinh-de-ban";
    boolean isNeedFeaturedProducts = false;
}
