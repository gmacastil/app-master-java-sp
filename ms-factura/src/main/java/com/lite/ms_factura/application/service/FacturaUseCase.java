package com.lite.ms_factura.application.service;

import java.util.List;
import java.util.Optional;

import com.lite.ms_factura.domain.model.Factura;

public interface FacturaUseCase {
    
    Factura createFactura(Factura factura);
    
    Optional<Factura> getFacturaById(Long id);
    
    List<Factura> getAllFacturas();
    
    Factura updateFactura(Long id, Factura facturaDetails);
    
    void deleteFactura(Long id);
    
    List<Factura> getFacturasByClienteId(Long clienteId);
}
