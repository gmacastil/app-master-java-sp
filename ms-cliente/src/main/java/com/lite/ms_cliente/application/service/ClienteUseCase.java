package com.lite.ms_cliente.application.service;

import java.util.List;
import java.util.Optional;

import com.lite.ms_cliente.domain.model.Cliente;

public interface ClienteUseCase {
    
    Cliente createCliente(Cliente cliente);
    
    Optional<Cliente> getClienteById(Long id);
    
    List<Cliente> getAllClientes();
    
    Cliente updateCliente(Long id, Cliente clienteDetails);
    
    void deleteCliente(Long id);
    
    Optional<Cliente> getClienteByEmail(String email);
}
