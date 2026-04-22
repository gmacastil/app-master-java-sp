package com.lite.ms_cliente.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FacturaDTO(
    Long id,
    String numero,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaEmision,
    BigDecimal total,
    @JsonProperty("clienteId")
    Long clienteId,
    String descripcion
) {}
