package com.example.authService.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.NumericChars;
import net.jqwik.api.constraints.StringLength;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordPropertyTest {

    private final Validator validator;

    public PasswordPropertyTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Property
    void validPasswordsShouldPassValidation(
            @ForAll
            @StringLength(min = 6, max = 62) // +2 символа = максимум 64
            @AlphaChars
            String randomString) {

        String validPassword = "A" + randomString + "1!";

        UserRegistrationRequest request = new UserRegistrationRequest(
                "TestUser", "test@example.com", validPassword
        );

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // ★ Ищем ошибку ТОЛЬКО по паттерну (по тексту сообщения), а не любую ошибку поля password
        boolean hasPatternError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")
                        && v.getMessage().contains("заглавную букву"));

        assertThat(hasPatternError).isFalse();
    }

    @Property
    void passwordsWithoutUppercaseShouldFail(
            @ForAll
            @StringLength(min = 6, max = 62)
            @NumericChars
            String randomDigits) {

        String invalidPassword = randomDigits + "!";

        UserRegistrationRequest request = new UserRegistrationRequest(
                "TestUser", "test@example.com", invalidPassword
        );

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        boolean hasPasswordError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));

        assertThat(hasPasswordError).isTrue();
    }
}