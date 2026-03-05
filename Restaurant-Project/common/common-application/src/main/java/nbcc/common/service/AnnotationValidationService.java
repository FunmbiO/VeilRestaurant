package nbcc.common.service;

import nbcc.common.validation.ValidationError;

import java.util.Collection;

public interface AnnotationValidationService {
    Collection<ValidationError> validate(Object object);
}