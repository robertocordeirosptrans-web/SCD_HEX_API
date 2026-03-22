package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.MarketingDistribuitionChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.MarketingDistribuitionChannelKeyEntityJpa;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MarketingDistribuitionChannelMapper {
    MarketingDistribuitionChannelMapper INSTANCE = Mappers.getMapper(MarketingDistribuitionChannelMapper.class);

    @Mapping(source = "id", target = "id")
    MarketingDistribuitionChannel toDomain(MarketingDistribuitionChannelEntityJpa entity);

    @Mapping(source = "id", target = "id")
    MarketingDistribuitionChannelEntityJpa toEntity(MarketingDistribuitionChannel domain);

    MarketingDistribuitionChannelKey toDomainKey(MarketingDistribuitionChannelKeyEntityJpa entityKey);
    MarketingDistribuitionChannelKeyEntityJpa toEntityKey(MarketingDistribuitionChannelKey domainKey);
}
