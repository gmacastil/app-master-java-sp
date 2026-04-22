package com.lite.ms_cliente.infrastructure.controller;

import com.lite.ms_cliente.application.exception.ResourceNotFoundException;
import com.lite.ms_cliente.application.mapper.ClienteMapper;
import com.lite.ms_cliente.application.service.ClienteUseCase;
import com.lite.ms_cliente.domain.dto.ClienteDTO;
import com.lite.ms_cliente.domain.dto.FacturaDTO;
import com.lite.ms_cliente.domain.model.Cliente;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;
    private final ClienteMapper clienteMapper;

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
    
    @PostMapping
    public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.toModel(clienteDTO);
        Cliente createdCliente = clienteUseCase.createCliente(cliente);

        log.info(null, value("cliente", createdCliente));
        return new ResponseEntity<>(clienteMapper.toDto(createdCliente), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        List<Cliente> clientes = clienteUseCase.getAllClientes();
        return ResponseEntity.ok(clienteMapper.toDtoList(clientes));
    }
   
    @GetMapping("/{idCli}")
    public ResponseEntity<ClienteDTO> getClienteByIdCli(@PathVariable String idCli) {
        Cliente cliente = clienteUseCase.getClienteByIdCli(idCli)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "idCli", idCli));
        log.info(null, value("cliente", cliente));
        return ResponseEntity.ok(clienteMapper.toDto(cliente));
    }
    
    @PutMapping("/{idCli}")
    public ResponseEntity<ClienteDTO> updateCliente(@PathVariable String idCli, @Valid @RequestBody ClienteDTO clienteDTO) {
        clienteUseCase.getClienteByIdCli(idCli)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "idCli", idCli));

        Cliente clienteDetails = clienteMapper.toModel(clienteDTO);
        Cliente updatedCliente = clienteUseCase.updateCliente(idCli, clienteDetails);
        return ResponseEntity.ok(clienteMapper.toDto(updatedCliente));
    }
    
    @DeleteMapping("/{idCli}")
    public ResponseEntity<?> deleteCliente(@PathVariable String idCli) {
        clienteUseCase.getClienteByIdCli(idCli)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "idCli", idCli));

        clienteUseCase.deleteCliente(idCli);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/email")
    public ResponseEntity<ClienteDTO> getClienteByEmail(@RequestParam String email) {
        Cliente cliente = clienteUseCase.getClienteByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "email", email));
        return ResponseEntity.ok(clienteMapper.toDto(cliente));
    }

    @GetMapping("/{idCli}/facturas")
    public ResponseEntity<List<FacturaDTO>> getFacturasByClienteId(@PathVariable String idCli) {
        // Verificar que el cliente existe
        clienteUseCase.getClienteByIdCli(idCli)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "idCli", idCli));

        // Delegar la consulta de facturas al use case (usa idCli para consultar el microservicio de facturas)
        List<FacturaDTO> facturas = clienteUseCase.getFacturasByClienteId(idCli);
        log.info("Consulta de facturas para cliente idCli: {}, facturas encontradas: {}",
                idCli, facturas.size());
        return ResponseEntity.ok(facturas);
    }
}
