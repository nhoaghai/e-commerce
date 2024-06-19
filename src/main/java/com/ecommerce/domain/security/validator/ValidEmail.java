package com.ecommerce.domain.security.validator;

import com.ecommerce.domain.security.validator.Impl.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Invalid email address. Valid e-mail can contain only latin letters, numbers, '@' and '.'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
