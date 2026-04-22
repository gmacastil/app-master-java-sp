package com.lite.ms_cliente.infrastructure.client;

import com.lite.ms_cliente.domain.dto.FacturaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "facturas-service",
    url = "${facturas.service.url}"
)
public interface FacturasFeignClient {

    @GetMapping("/v1/facturas/cliente/{clienteId}")
    List<FacturaDTO> getFacturasByClienteId(@PathVariable("clienteId") Long clienteId);
}
