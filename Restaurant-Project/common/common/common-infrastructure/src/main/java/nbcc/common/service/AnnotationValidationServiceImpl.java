package nbcc.common.service;

import nbcc.common.validation.ValidationError;
import org.springframework.stereotype.Service;
import org.springframework.validation.*;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class AnnotationValidationServiceImpl implements AnnotationValidationService {

    private final Validator validator;

    public AnnotationValidationServiceImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public Collection<ValidationError> validate(Object object) {
        return validate(object, object.getClass().getSimpleName());
    }

    protected Collection<ValidationError> validate(Object object, String objectName) {

        if (validator.supports(object.getClass())) {
            var propertyErrors = new BeanPropertyBindingResult(object, objectName);
            validator.validate(object, propertyErrors); // populates property errors based on annotations
            return convertToValidationErrors(propertyErrors);
        }

        return new ArrayList<>();
    }

    private Collection<ValidationError> convertToValidationErrors(Errors errors) {

        var validationErrors = new ArrayList<ValidationError>();

        if (errors.hasErrors()) {
            for (ObjectError objectError : errors.getGlobalErrors()) {
                validationErrors.add(new ValidationError(objectError.getDefaultMessage()));
            }

            for (FieldError fieldError : errors.getFieldErrors()) {
                validationErrors.add(new ValidationError(fieldError.getDefaultMessage(), fieldError.getField(), fieldError.getRejectedValue()));
            }
        }

        return validationErrors;
    }
}
