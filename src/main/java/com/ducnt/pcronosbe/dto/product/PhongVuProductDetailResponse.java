package com.ducnt.pcronosbe.dto.product;

import com.ducnt.pcronosbe.dto.attribute.AttributeDTO;
import com.ducnt.pcronosbe.dto.image.ImageDTO;
import com.ducnt.pcronosbe.dto.unitOfMeasures.UnitOfMeasuresDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PhongVuProductDetailResponse {
    String description;
    @JsonProperty("attributeGroups")
    List<AttributeDTO> attributeDTOS;
    @JsonProperty("images")
    List<ImageDTO> imageDTOS;
    @JsonProperty("attributes")
    List<UnitOfMeasuresDTO> unitOfMeasuresDTOS;

    public static PhongVuProductDetailResponse onCreation(Object result) {
        ObjectMapper objectMapper = new ObjectMapper();
        PhongVuProductDetailResponse response = new PhongVuProductDetailResponse();

        try {
            JsonNode resultNode = objectMapper.valueToTree(result);
            JsonNode productNode = resultNode.path("product");
            if (productNode.isMissingNode()) return null;
            JsonNode productDetailNode = productNode.path("productDetail");
            if (productDetailNode.isMissingNode()) return null;
            response.description = productDetailNode.path("description").asText();
            response.attributeDTOS = objectMapper.convertValue(productDetailNode.path("attributeGroups"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, AttributeDTO.class));

            response.imageDTOS = objectMapper.convertValue(productDetailNode.path("images"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ImageDTO.class));

            response.unitOfMeasuresDTOS = objectMapper.convertValue(productDetailNode.path("attributes"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, UnitOfMeasuresDTO.class));
        } catch (Exception e) {
            return null;
        }
        return response;
    }
}
