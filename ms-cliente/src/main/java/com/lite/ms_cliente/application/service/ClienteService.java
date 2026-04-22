package com.lite.ms_cliente.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lite.ms_cliente.application.exception.ResourceNotFoundException;
import com.lite.ms_cliente.domain.dto.FacturaDTO;
import com.lite.ms_cliente.domain.model.Cliente;
import com.lite.ms_cliente.infrastructure.client.FacturasFeignClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final FacturasFeignClient facturasFeignClient;
    
    @Override
    public Cliente createCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    
    @Override
    public Optional<Cliente> getClienteById(String id) {
        return clienteRepository.findById(id);
    }
    
    @Override
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }
    
    @Override
    public Cliente updateCliente(String idCli, Cliente clienteDetails) {
        Cliente existingCliente = clienteRepository.findByIdCli(idCli)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "idCli", idCli));

        Cliente clienteToUpdate = new Cliente(
                existingCliente.id(),  // Mantener el ID de MongoDB
                idCli,
                clienteDetails.nombre(),
                clienteDetails.apellido(),
                clienteDetails.email(),
                clienteDetails.celular(),
                clienteDetails.direccion(),
                clienteDetails.fechaRegistro());

        return clienteRepository.save(clienteToUpdate);
    }
    
    @Override
    public void deleteCliente(String idCli) {
        Cliente cliente = clienteRepository.findByIdCli(idCli)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "idCli", idCli));
        clienteRepository.deleteById(cliente.id());  // Usar el ID de MongoDB para eliminar
    }
    
    @Override
    public Optional<Cliente> getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    public Optional<Cliente> getClienteByIdCli(String idCli) {
        return clienteRepository.findByIdCli(idCli);
    }

    @Override
    public List<FacturaDTO> getFacturasByClienteId(String clienteId) {
        try {
            // Convertir el ID de String a Long para el servicio de facturas
            Long clienteIdLong = Long.parseLong(clienteId);

            log.info("Consultando facturas para clienteId: {}", clienteId);
            List<FacturaDTO> facturas = facturasFeignClient.getFacturasByClienteId(clienteIdLong);

            log.info("Se encontraron {} facturas para clienteId: {}",
                    facturas != null ? facturas.size() : 0, clienteId);

            return facturas != null ? facturas : Collections.emptyList();

        } catch (NumberFormatException e) {
            log.warn("ID de cliente no es numérico: {}", clienteId);
            throw new IllegalArgumentException("El ID del cliente debe ser numérico para consultar facturas");
        } catch (FeignException.NotFound e) {
            log.warn("No se encontraron facturas para clienteId: {}", clienteId);
            return Collections.emptyList();
        } catch (FeignException e) {
            log.error("Error al consultar facturas para clienteId: {} - Status: {}",
                    clienteId, e.status(), e);
            throw new RuntimeException("Error al consultar el servicio de facturas: " + e.getMessage(), e);
        }
    }
}
