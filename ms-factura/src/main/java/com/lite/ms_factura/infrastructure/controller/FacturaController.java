package com.lite.ms_factura.infrastructure.controller;

import com.lite.ms_factura.application.exception.ResourceNotFoundException;
import com.lite.ms_factura.application.mapper.FacturaMapper;
import com.lite.ms_factura.application.service.FacturaUseCase;
import com.lite.ms_factura.domain.dto.FacturaDTO;
import com.lite.ms_factura.domain.model.Factura;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/v1/facturas")
@RequiredArgsConstructor
public class FacturaController {
    
    private final FacturaUseCase facturaUseCase;
    private final FacturaMapper facturaMapper;

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);
    
    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(@Valid @RequestBody FacturaDTO facturaDTO) {
        Factura factura = facturaMapper.toModel(facturaDTO);
        Factura createdFactura = facturaUseCase.createFactura(factura);

        log.info(null, value("factura", createdFactura));
        return new ResponseEntity<>(facturaMapper.toDto(createdFactura), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<FacturaDTO>> getAllFacturas() {
        List<Factura> facturas = facturaUseCase.getAllFacturas();
        return ResponseEntity.ok(facturaMapper.toDtoList(facturas));
    }
   
    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(@PathVariable Long id) {
        Factura factura = facturaUseCase.getFacturaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        log.info(null, value("factura", factura));
        return ResponseEntity.ok(facturaMapper.toDto(factura));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(@PathVariable Long id, @Valid @RequestBody FacturaDTO facturaDTO) {
        // Verificar si la factura existe
        facturaUseCase.getFacturaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        
        Factura facturaDetails = facturaMapper.toModel(facturaDTO);
        Factura updatedFactura = facturaUseCase.updateFactura(id, facturaDetails);
        return ResponseEntity.ok(facturaMapper.toDto(updatedFactura));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFactura(@PathVariable Long id) {
        // Verificar si la factura existe
        facturaUseCase.getFacturaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        
        facturaUseCase.deleteFactura(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<FacturaDTO>> getFacturasByClienteId(@PathVariable Long clienteId) {
        List<Factura> facturas = facturaUseCase.getFacturasByClienteId(clienteId);
        return ResponseEntity.ok(facturaMapper.toDtoList(facturas));
    }
}
