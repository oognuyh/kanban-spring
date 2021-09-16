package com.oognuyh.kanban.error.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;

@Getter
public class ValidationException extends ResponseStatusException {
    private Map<String, String> violations = new HashMap<>();

    public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
        super(HttpStatus.BAD_REQUEST);
        
        violations.stream()
            .forEach(violation -> 
                this.violations.put(((PathImpl) violation.getPropertyPath()).getLeafNode().toString(), violation.getMessage()));
    }
}
