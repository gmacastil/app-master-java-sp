package com.lite.ms_cliente.infrastructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Document(collection = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

    @Id
    private String id;

    @Field("id_cli")
    private String idCli;

    @Field("nombre")
    private String nombre;

    @Field("apellido")
    private String apellido;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("celular")
    private String celular;

    @Field("direccion")
    private String direccion;

    @Field("fecha_registro")
    private LocalDate fechaRegistro;
}
