package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.ChannelFeeEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.ChannelFeeKeyEntityJpa;
import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface ChannelFeeMapper {

    @Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
    ChannelFee toDomain(ChannelFeeEntityJpa entity);

    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    ChannelFeeEntityJpa toEntity(ChannelFee fee);

    ChannelFeeKey toDomainKey(ChannelFeeKeyEntityJpa entityKey);

    ChannelFeeKeyEntityJpa toEntityKey(ChannelFeeKey domainKey);
}
