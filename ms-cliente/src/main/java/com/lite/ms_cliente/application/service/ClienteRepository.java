package com.lite.ms_cliente.application.service;

import java.util.List;
import java.util.Optional;

import com.lite.ms_cliente.domain.model.Cliente;

public interface ClienteRepository {
    
    Cliente save(Cliente cliente);
    
    Optional<Cliente> findById(Long id);
    
    List<Cliente> findAll();
    
    void deleteById(Long id);
    
    Optional<Cliente> findByEmail(String email);
}
