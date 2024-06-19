package com.ecommerce.domain.security.dto.request;

import com.ecommerce.domain.security.validator.PasswordMatching;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatching(
        password = "newPassword",
        confirmPassword = "confirmPassword",
        message = "New password and confirm password must matched!"
)
public class ChangePasswordRequest {
    @NotBlank(message = "Please enter your old password")
    private String oldPassword;

    @NotBlank(message = "Please enter your new password")
    private String newPassword;

    @NotBlank(message = "Please enter your confirm password")
    private String confirmPassword;
}
