package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.FareEntityJpa;
import br.sptrans.scd.product.domain.Fare;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface FareMapper {

	@Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
	@Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
	@Mapping(target = "codVersao", ignore = true)
	Fare toDomain(FareEntityJpa entity);

	@Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
	@Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
	FareEntityJpa toEntity(Fare fare);
}
