package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.FeeEntityJpa;
import br.sptrans.scd.product.domain.Fee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FeeMapper {

	@Mapping(source = "dtFim", target = "dtFinal")
	@Mapping(target = "canal", ignore = true)
	@Mapping(target = "produto", ignore = true)
	@Mapping(target = "taxaAdministrativa", ignore = true)
	@Mapping(target = "taxaServico", ignore = true)
	@Mapping(target = "taxaDes", ignore = true)
	Fee toDomain(FeeEntityJpa entity);

	@Mapping(source = "dtFinal", target = "dtFim")
	FeeEntityJpa toEntity(Fee fee);
}
