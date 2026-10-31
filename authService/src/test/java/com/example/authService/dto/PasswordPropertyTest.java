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

    // ★ СВОЙСТВО 1: Любой пароль из правильных символов (Заглавная + цифра + спецсимвол)
    // и длиной от 8 до 64 — должен проходить валидацию.
    @Property
    void validPasswordsShouldPassValidation(
            @ForAll
            @StringLength(min = 8, max = 64)
            @AlphaChars
            String randomString) {

        // Гарантируем, что есть заглавная, цифра и спецсимвол
        String validPassword = "A" + randomString + "1!";

        UserRegistrationRequest request = new UserRegistrationRequest(
                "TestUser", "test@example.com", validPassword
        );

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        // Нас интересуют только ошибки, связанные с паролем
        boolean hasPasswordError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));

        assertThat(hasPasswordError).isFalse();
    }

    // ★ СВОЙСТВО 2: Любая строка БЕЗ заглавной буквы (только строчные и цифры)
    // должна падать с ошибкой валидации.
    @Property
    void passwordsWithoutUppercaseShouldFail(
            @ForAll
            @StringLength(min = 8, max = 64)
            @NumericChars
            String randomDigits) {

        // Только цифры (нет заглавных букв и спецсимволов)
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