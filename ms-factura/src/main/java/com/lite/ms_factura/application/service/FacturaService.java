package com.lite.ms_factura.application.service;

import org.springframework.stereotype.Service;

import com.lite.ms_factura.domain.model.Factura;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacturaService implements FacturaUseCase {

    private final FacturaRepository facturaRepository;
    
    @Override
    public Factura createFactura(Factura factura) {
        return facturaRepository.save(factura);
    }
    
    @Override
    public Optional<Factura> getFacturaById(Long id) {
        return facturaRepository.findById(id);
    }
    
    @Override
    public List<Factura> getAllFacturas() {
        return facturaRepository.findAll();
    }
    
    @Override
    public Factura updateFactura(Long id, Factura facturaDetails) {
        Optional<Factura> facturaOpt = facturaRepository.findById(id);
        
        if (facturaOpt.isPresent()) {
            Factura existingFactura = facturaOpt.get();
            facturaDetails.setId(id);
            return facturaRepository.save(facturaDetails);
        } else {
            throw new RuntimeException("Factura no encontrada con id: " + id);
        }
    }
    
    @Override
    public void deleteFactura(Long id) {
        facturaRepository.deleteById(id);
    }
    
    @Override
    public List<Factura> getFacturasByClienteId(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }


}
