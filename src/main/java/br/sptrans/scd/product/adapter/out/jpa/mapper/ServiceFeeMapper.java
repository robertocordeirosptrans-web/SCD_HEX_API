package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.ServiceFeeEntityJpa;
import br.sptrans.scd.product.domain.ServiceFee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceFeeMapper {

    @Mapping(target = "taxa", ignore = true)
    ServiceFee toDomain(ServiceFeeEntityJpa entity);

    ServiceFeeEntityJpa toEntity(ServiceFee fee);
}
