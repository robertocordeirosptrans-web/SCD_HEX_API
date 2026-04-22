package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.SpeciesEntityJpa;
import br.sptrans.scd.product.domain.Species;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface SpeciesMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
	@Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
	Species toDomain(SpeciesEntityJpa entity);

	@Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
	@Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
	SpeciesEntityJpa toEntity(Species domain);
}
