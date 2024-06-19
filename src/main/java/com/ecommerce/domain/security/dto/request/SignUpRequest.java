package com.ecommerce.domain.security.dto.request;

import com.ecommerce.domain.security.validator.ExitEmail;
import com.ecommerce.domain.security.validator.PasswordMatching;
import com.ecommerce.domain.security.validator.StrongPassword;
import com.ecommerce.domain.security.validator.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatching(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "Password and Confirm Password must be matched!"
)
public class SignUpRequest {

    @NotBlank(message = "Please enter your first name")
    private String firstName;

    @NotBlank(message = "Please enter your last name")
    private String lastName;

    @ExitEmail
    @ValidEmail
    @NotBlank(message = "Please enter your email")
    private String email;

    @NotBlank(message = "Please enter your phone number")
    private String phoneNumber;

    private LocalDate birthDate;

    @StrongPassword
    @NotBlank(message = "Please enter your password")
    private String password;

    @NotBlank(message = "Please enter your confirm password")
    private String confirmPassword;
}
