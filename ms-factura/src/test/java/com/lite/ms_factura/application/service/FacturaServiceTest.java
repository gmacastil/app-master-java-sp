package com.lite.ms_factura.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lite.ms_factura.domain.model.Factura;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private FacturaService facturaService;

    @Test
    void createFacturaShouldSaveAndReturnCreatedFactura() {
        Factura facturaToCreate = new Factura(
                null,
                "FACT-2026-001",
                LocalDate.of(2026, 4, 9),
                new BigDecimal("1500.50"),
                1L,
                "Factura por servicios de consultoria");

        Factura facturaSaved = new Factura(
                1L,
                "FACT-2026-001",
                LocalDate.of(2026, 4, 9),
                new BigDecimal("1500.50"),
                1L,
                "Factura por servicios de consultoria");

        when(facturaRepository.save(facturaToCreate)).thenReturn(facturaSaved);

        Factura result = facturaService.createFactura(facturaToCreate);

        assertSame(facturaSaved, result);
        assertEquals(1L, result.id());
        assertEquals("FACT-2026-001", result.numero());
        verify(facturaRepository).save(facturaToCreate);
    }

        @Test
        void updateFacturaShouldUsePathIdAndSaveUpdatedData() {
        Long facturaId = 7L;

        Factura existingFactura = new Factura(
            facturaId,
            "FACT-ORIG-007",
            LocalDate.of(2026, 4, 1),
            new BigDecimal("800.00"),
            3L,
            "Factura original");

        Factura facturaDetails = new Factura(
            999L,
            "FACT-2026-007-MOD",
            LocalDate.of(2026, 4, 9),
            new BigDecimal("950.00"),
            3L,
            "Factura actualizada");

        Factura facturaSaved = new Factura(
            facturaId,
            "FACT-2026-007-MOD",
            LocalDate.of(2026, 4, 9),
            new BigDecimal("950.00"),
            3L,
            "Factura actualizada");

        when(facturaRepository.findById(facturaId)).thenReturn(Optional.of(existingFactura));
        when(facturaRepository.save(org.mockito.ArgumentMatchers.any(Factura.class))).thenReturn(facturaSaved);

        Factura result = facturaService.updateFactura(facturaId, facturaDetails);

        assertNotNull(result);
        assertEquals(facturaId, result.id());
        assertEquals("FACT-2026-007-MOD", result.numero());
        verify(facturaRepository).findById(facturaId);
        verify(facturaRepository).save(org.mockito.ArgumentMatchers.argThat(f ->
            f.id().equals(facturaId)
                && f.numero().equals("FACT-2026-007-MOD")
                && f.total().compareTo(new BigDecimal("950.00")) == 0));
        }
}
