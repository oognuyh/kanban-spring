package com.oognuyh.kanban.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.oognuyh.kanban.error.exception.ValidationException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorUtils {
    private final Validator validator;

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {      
            log.error("validationexception occurs\n {}", violations);
            throw new ValidationException(violations);
        }
    }
}
