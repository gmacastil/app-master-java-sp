package com.lite.ms_factura.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LogDTO (
    Long id,
    String numero,
    LocalDate fechaEmision,
    BigDecimal total,
    Long clienteId,
    String descripcion,
    LocalDate fechaCreacion,
    LocalDate fechaActualizacion,
    String accion
) {
}