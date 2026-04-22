package com.lite.ms_cliente.application.service;

import java.util.List;
import java.util.Optional;

import com.lite.ms_cliente.domain.model.Cliente;

public interface ClienteRepository {
    
    Cliente save(Cliente cliente);
    
    Optional<Cliente> findById(String id);
    
    List<Cliente> findAll();
    
    void deleteById(String id);
    
    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByIdCli(String idCli);
}
