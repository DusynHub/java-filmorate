package ru.yandex.practicum.javafilmorate.service;

import org.springframework.stereotype.Service;

import javax.validation.*;
import java.util.Set;

@Service
public class ValidatingServiceForTesting {

    private final Validator validator;

    ValidatingServiceForTesting() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    void validateInputWithInjectedValidator(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
