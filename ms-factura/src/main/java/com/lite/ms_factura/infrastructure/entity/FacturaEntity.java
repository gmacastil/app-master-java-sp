package com.lite.ms_factura.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;


import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numero;
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    
    @Column(nullable = false)
    private BigDecimal total;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    private String descripcion;
}
