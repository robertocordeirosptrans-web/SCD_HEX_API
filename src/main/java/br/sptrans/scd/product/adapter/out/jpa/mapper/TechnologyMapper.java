package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.TechnologyEntityJpa;
import br.sptrans.scd.product.domain.Technology;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface TechnologyMapper {

    @Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
    @Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
    Technology toDomain(TechnologyEntityJpa entity);

    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    TechnologyEntityJpa toEntity(Technology tech);
}
