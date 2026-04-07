package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.AdministrativeFeeEntityJpa;
import br.sptrans.scd.product.domain.AdministrativeFee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdministrativeFeeMapper {

	@Mapping(target = "taxa", ignore = true)
	AdministrativeFee toDomain(AdministrativeFeeEntityJpa entity);

	AdministrativeFeeEntityJpa toEntity(AdministrativeFee fee);
}
