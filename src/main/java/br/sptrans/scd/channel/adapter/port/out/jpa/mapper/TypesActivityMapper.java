package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.domain.TypesActivity;

@Mapper(componentModel = "spring")
public interface TypesActivityMapper {
    TypesActivityMapper INSTANCE = Mappers.getMapper(TypesActivityMapper.class);

    TypesActivity toDomain(TypesActivityEntityJpa entity);
    TypesActivityEntityJpa toEntity(TypesActivity domain);
}
