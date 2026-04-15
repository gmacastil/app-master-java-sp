package com.lite.ms_cliente.application.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CelularValidator implements ConstraintValidator<Celular, String> {

    @Override
    public void initialize(Celular constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String celular, ConstraintValidatorContext context) {
        // Permitir valores null (usar @NotNull si es requerido)
        if (celular == null) {
            return true;
        }

        // Verificar que tenga exactamente 12 dígitos
        if (celular.length() != 12) {
            return false;
        }

        // Verificar que empiece por 3
        if (!celular.startsWith("3")) {
            return false;
        }

        // Verificar que todos los caracteres sean dígitos
        return celular.matches("\\d{12}");
    }
}
