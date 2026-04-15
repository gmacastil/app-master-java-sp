package com.lite.ms_factura.application.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CodigoFacturaValidator implements ConstraintValidator<CodigoFactura, String> {

    @Override
    public void initialize(CodigoFactura constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String codigo, ConstraintValidatorContext context) {
        // Permitir valores null (usar @NotNull si es requerido)
        if (codigo == null) {
            return true;
        }

        // Verificar formato: FACT-YYYY-NNN
        // Ejemplo: FACT-2026-001
        return codigo.matches("^FACT-\\d{4}-\\d{3}$");
    }
}
