package com.ducnt.pcronosbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Product extends BaseModel {
    String name;
    @Column(columnDefinition = "TEXT")
    String description;
    double price;
    String vendor;
    String image;
    String canonical;
    @Column(columnDefinition = "TEXT")
    String bodyHtml;
}
