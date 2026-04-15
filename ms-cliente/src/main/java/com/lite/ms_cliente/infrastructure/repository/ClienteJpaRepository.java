package com.lite.ms_cliente.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lite.ms_cliente.infrastructure.entity.ClienteEntity;

import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    
    Optional<ClienteEntity> findByEmail(String email);
}
