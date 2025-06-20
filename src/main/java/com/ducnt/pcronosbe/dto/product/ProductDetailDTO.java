package com.ducnt.pcronosbe.dto.product;

import com.ducnt.pcronosbe.dto.attribute.AttributeDTO;
import com.ducnt.pcronosbe.dto.image.ImageDTO;
import com.ducnt.pcronosbe.dto.unitOfMeasures.UnitOfMeasuresDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailDTO {
    String name;
    String shortDescription;
    double price;
    String vendor;
    String avatar;
    String canonical;
    String bodyHtml;
    String description;
    List<AttributeDTO> attributeDTOS;
    List<ImageDTO> imageDTOS;
    List<UnitOfMeasuresDTO> unitOfMeasuresDTOS;
}
