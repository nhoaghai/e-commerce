package com.ecommerce.domain.security.validator.Impl;

import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.validator.ExitEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExistEmailValidator implements ConstraintValidator<ExitEmail, String> {
    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && !memberRepository.existsByEmail(email);
    }
}
