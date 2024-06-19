package com.ecommerce.domain.security.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponse {
    private String id;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private LocalDateTime expiryDate;
    private String avatarUrl;
    private String tokenType;
}
