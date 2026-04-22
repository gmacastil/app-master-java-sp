package com.lite.ms_cliente.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.lite.ms_cliente.infrastructure.entity.ClienteEntity;

import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends MongoRepository<ClienteEntity, String> {

    Optional<ClienteEntity> findByEmail(String email);

    Optional<ClienteEntity> findByIdCli(String idCli);
}
