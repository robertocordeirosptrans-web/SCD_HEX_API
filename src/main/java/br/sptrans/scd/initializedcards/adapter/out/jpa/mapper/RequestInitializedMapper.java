package br.sptrans.scd.initializedcards.adapter.out.jpa.mapper;

import br.sptrans.scd.initializedcards.adapter.out.persistence.entity.RICEntityJpa;
import br.sptrans.scd.initializedcards.domain.RequestInitializedCards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RequestInitializedMapper {

    RequestInitializedMapper INSTANCE = Mappers.getMapper(RequestInitializedMapper.class);

    @Mapping(target = "codCanal", ignore = true)
    @Mapping(target = "codProduto", ignore = true)
    @Mapping(target = "idUsuarioAprovacao", ignore = true)
    @Mapping(target = "idUsuarioCadastro", ignore = true)
    @Mapping(target = "idUsuarioManutencao", ignore = true)
    RequestInitializedCards toDomain(RICEntityJpa entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codProduto", ignore = true)
    @Mapping(target = "idUsuarioAprovacao", ignore = true)
    @Mapping(target = "idUsuarioCadastro", ignore = true)
    @Mapping(target = "idUsuarioManutencao", ignore = true)
    RICEntityJpa toEntity(RequestInitializedCards domain);
}
