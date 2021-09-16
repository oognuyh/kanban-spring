package com.oognuyh.kanban.error;

import java.util.Map;

import com.oognuyh.kanban.error.exception.ValidationException;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        
        Throwable throwable = getError(request);

        if (throwable instanceof ValidationException) {
            ValidationException exception = (ValidationException) throwable;
        
            exception.getViolations().entrySet().stream()
                .forEach(entry -> errorAttributes.put(entry.getKey(), entry.getValue()));
        }

        return errorAttributes;
    }
}
