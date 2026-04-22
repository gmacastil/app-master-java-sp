package com.lite.ms_cliente.domain.model;

import java.time.LocalDate;

public record Cliente(
    String id,
    String idCli,
    String nombre,
    String apellido,
    String email,
    String celular,
    String direccion,
    LocalDate fechaRegistro
) {}
