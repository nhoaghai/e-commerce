package com.ecommerce.domain.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "token")
public class Token {
    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(name = "token_value")
    private String tokenValue;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "expired", columnDefinition = "boolean default true")
    private Boolean expired;

    @Column(name = "revoke", columnDefinition = "boolean default false")
    private Boolean revoke;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

    @Column(name = "is_mobile", columnDefinition = "boolean default true")
    private Boolean isMobile;
}
