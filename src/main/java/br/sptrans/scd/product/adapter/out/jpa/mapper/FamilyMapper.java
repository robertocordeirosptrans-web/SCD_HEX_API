package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.FamilyEntityJpa;
import br.sptrans.scd.product.domain.Family;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface FamilyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
    @Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
    Family toDomain(FamilyEntityJpa entity);

    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    FamilyEntityJpa toEntity(Family family);
}
