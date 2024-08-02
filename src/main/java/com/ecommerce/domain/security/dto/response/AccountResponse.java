package com.ecommerce.domain.security.dto.response;

import com.ecommerce.domain.security.model.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private boolean isActive;
    private List<String> address;
    private Set<Role> roles;
}
