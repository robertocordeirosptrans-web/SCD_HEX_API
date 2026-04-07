package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.DestinyFeeEntityJpa;
import br.sptrans.scd.product.domain.DestinyFee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DestinyFeeMapper {

    DestinyFee toDomain(DestinyFeeEntityJpa entity);

    DestinyFeeEntityJpa toEntity(DestinyFee fee);
}
