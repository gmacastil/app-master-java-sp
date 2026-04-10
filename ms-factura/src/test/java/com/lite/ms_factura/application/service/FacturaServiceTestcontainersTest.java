package com.lite.ms_factura.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.lite.ms_factura.domain.model.Factura;

@Testcontainers
@SpringBootTest
class FacturaServiceTestcontainersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("factura_test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driverClassName", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private FacturaService facturaService;

    @Test
    void createFacturaShouldPersistDataInPostgresContainer() {
        Factura factura = new Factura(
                null,
                "FACT-TC-001",
                LocalDate.of(2026, 4, 9),
                new BigDecimal("1234.56"),
                42L,
                "Factura creada con Testcontainers");

        Factura created = facturaService.createFactura(factura);

        assertNotNull(created.id());
        assertTrue(created.id() > 0);
        assertTrue(facturaService.getFacturaById(created.id()).isPresent());
    }
}
