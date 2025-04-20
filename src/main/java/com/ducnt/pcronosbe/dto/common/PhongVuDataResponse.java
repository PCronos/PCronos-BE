package com.ducnt.pcronosbe.dto.common;

import com.ducnt.pcronosbe.dto.product.PhongVuProductDataResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.List;

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
}
