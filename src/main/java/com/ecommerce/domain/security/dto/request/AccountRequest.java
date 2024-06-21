package com.ecommerce.domain.security.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean gender;
    private LocalDate birthday;
}
