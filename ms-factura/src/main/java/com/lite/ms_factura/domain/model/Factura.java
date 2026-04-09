package com.lite.ms_factura.domain.model;


import java.math.BigDecimal;
import java.time.LocalDate;


public record Factura(
    Long id,
    String numero,
    LocalDate fechaEmision,
    BigDecimal total,
    Long clienteId,
    String descripcion
) {

    public void setId(Long id2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }
}
