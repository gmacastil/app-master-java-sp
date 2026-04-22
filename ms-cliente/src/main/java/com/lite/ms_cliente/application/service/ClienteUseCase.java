package com.lite.ms_cliente.application.service;

import java.util.List;
import java.util.Optional;

import com.lite.ms_cliente.domain.dto.FacturaDTO;
import com.lite.ms_cliente.domain.model.Cliente;

public interface ClienteUseCase {

    Cliente createCliente(Cliente cliente);

    Optional<Cliente> getClienteById(String id);

    List<Cliente> getAllClientes();

    Cliente updateCliente(String id, Cliente clienteDetails);

    void deleteCliente(String id);

    Optional<Cliente> getClienteByEmail(String email);

    Optional<Cliente> getClienteByIdCli(String idCli);

    List<FacturaDTO> getFacturasByClienteId(String clienteId);
}
