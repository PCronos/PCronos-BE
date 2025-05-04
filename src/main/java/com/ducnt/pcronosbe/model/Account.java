package com.ducnt.pcronosbe.model;

import com.ducnt.pcronosbe.enums.AccountRole;
import com.ducnt.pcronosbe.enums.AccountStatus;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Account extends BaseModel{
    String fullName;
    String email;
    String password;
    String phone;
    String address;
    LocalDate dob;
    String image;
    AccountStatus status;
    AccountRole role;
}
