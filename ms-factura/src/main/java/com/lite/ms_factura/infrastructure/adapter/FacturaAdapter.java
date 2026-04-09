package com.lite.ms_factura.infrastructure.adapter;

import org.springframework.stereotype.Component;

import com.lite.ms_factura.application.mapper.FacturaMapper;
import com.lite.ms_factura.application.service.FacturaRepository;
import com.lite.ms_factura.domain.model.Factura;
import com.lite.ms_factura.infrastructure.entity.FacturaEntity;
import com.lite.ms_factura.infrastructure.repository.FacturaJpaRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacturaAdapter implements FacturaRepository {
    
    private final FacturaJpaRepository facturaJpaRepository;
    private final FacturaMapper facturaMapper;
    
    @Override
    public Factura save(Factura factura) {
        FacturaEntity entity = facturaMapper.toEntity(factura);
        FacturaEntity savedEntity = facturaJpaRepository.save(entity);
        return facturaMapper.toModel(savedEntity);
    }
    
    @Override
    public Optional<Factura> findById(Long id) {
        return facturaJpaRepository.findById(id)
                .map(facturaMapper::toModel);
    }
    
    @Override
    public List<Factura> findAll() {
        return facturaJpaRepository.findAll().stream()
                .map(facturaMapper::toModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        facturaJpaRepository.deleteById(id);
    }
    
    @Override
    public List<Factura> findByClienteId(Long clienteId) {
        return facturaJpaRepository.findByClienteId(clienteId).stream()
                .map(facturaMapper::toModel)
                .collect(Collectors.toList());
    }
}
