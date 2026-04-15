package com.lite.ms_cliente.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.lite.ms_cliente.domain.dto.ClienteDTO;
import com.lite.ms_cliente.domain.model.Cliente;
import com.lite.ms_cliente.infrastructure.entity.ClienteEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);
    
    Cliente toModel(ClienteDTO dto);
    
    ClienteDTO toDto(Cliente model);
    
    List<ClienteDTO> toDtoList(List<Cliente> modelList);
    
    Cliente toModel(ClienteEntity entity);
    
    ClienteEntity toEntity(Cliente model);
    
    List<Cliente> toModelList(List<ClienteEntity> entityList);
}
