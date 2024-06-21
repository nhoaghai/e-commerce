package com.ecommerce.domain.security.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String memberId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean gender;
    private LocalDate birthday;
    private String avatarUrl;
    private List<String> address;
}
