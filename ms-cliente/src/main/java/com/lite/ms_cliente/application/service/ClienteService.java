package com.lite.ms_cliente.application.service;

import org.springframework.stereotype.Service;

import com.lite.ms_cliente.application.exception.ResourceNotFoundException;
import com.lite.ms_cliente.application.service.ClienteRepository;
import com.lite.ms_cliente.domain.model.Cliente;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteRepository clienteRepository;
    
    @Override
    public Cliente createCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    
    @Override
    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }
    
    @Override
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }
    
    @Override
    public Cliente updateCliente(Long id, Cliente clienteDetails) {
        clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        Cliente clienteToUpdate = new Cliente(
                id,
                clienteDetails.nombre(),
                clienteDetails.apellido(),
                clienteDetails.email(),
                clienteDetails.celular(),
                clienteDetails.direccion(),
                clienteDetails.fechaRegistro());

        return clienteRepository.save(clienteToUpdate);
    }
    
    @Override
    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }
    
    @Override
    public Optional<Cliente> getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }
}
