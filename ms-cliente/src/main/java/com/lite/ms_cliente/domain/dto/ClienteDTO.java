package com.lite.ms_cliente.domain.dto;

import com.lite.ms_cliente.application.validators.Celular;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClienteDTO(
    Long id,
    @NotEmpty(message = "El nombre no puede estar vacío")
    String nombre,
    @NotEmpty(message = "El apellido no puede estar vacío")
    String apellido,
    @NotNull(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    String email,
    @Celular(message = "El número de celular debe tener 12 dígitos, empezar por 3 y contener solo números")
    String celular,
    String direccion,
    LocalDate fechaRegistro
) {}
