package com.lite.ms_factura.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;
import java.time.LocalDate;


public record FacturaDTO (
    Long id,
    @NotEmpty(message = "El número de factura no puede estar vacío")
    String numero,
    @NotNull(message = "La fecha de emisión es obligatoria")
    LocalDate fechaEmision,
    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser un valor positivo")
    BigDecimal total,
    @NotNull(message = "El ID del cliente es obligatorio")
    Long clienteId,
    String descripcion
) {
}
