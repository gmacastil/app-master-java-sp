package com.lite.ms_cliente.infrastructure.controller;

import com.lite.ms_cliente.application.exception.ResourceNotFoundException;
import com.lite.ms_cliente.application.mapper.ClienteMapper;
import com.lite.ms_cliente.application.service.ClienteUseCase;
import com.lite.ms_cliente.domain.dto.ClienteDTO;
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
   
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable Long id) {
        Cliente cliente = clienteUseCase.getClienteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        log.info(null, value("cliente", cliente));
        return ResponseEntity.ok(clienteMapper.toDto(cliente));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        clienteUseCase.getClienteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        Cliente clienteDetails = clienteMapper.toModel(clienteDTO);
        Cliente updatedCliente = clienteUseCase.updateCliente(id, clienteDetails);
        return ResponseEntity.ok(clienteMapper.toDto(updatedCliente));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
        clienteUseCase.getClienteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        clienteUseCase.deleteCliente(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/email")
    public ResponseEntity<ClienteDTO> getClienteByEmail(@RequestParam String email) {
        Cliente cliente = clienteUseCase.getClienteByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "email", email));
        return ResponseEntity.ok(clienteMapper.toDto(cliente));
    }
}
