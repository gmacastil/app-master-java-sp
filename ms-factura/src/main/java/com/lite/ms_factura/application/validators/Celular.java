package com.lite.ms_factura.application.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CelularValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Celular {

    String message() default "El número de celular debe empezar por 3 y tener 12 dígitos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
