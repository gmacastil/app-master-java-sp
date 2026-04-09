package com.lite.ms_factura.application.service;

import java.util.List;
import java.util.Optional;

import com.lite.ms_factura.domain.model.Factura;

public interface FacturaRepository {
    
    Factura save(Factura factura);
    
    Optional<Factura> findById(Long id);
    
    List<Factura> findAll();
    
    void deleteById(Long id);
    
    List<Factura> findByClienteId(Long clienteId);

}
