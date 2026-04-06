package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.MarketingDistribuitionChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.MarketingDistribuitionChannelKeyEntityJpa;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring")
public interface MarketingDistribuitionChannelMapper {

    MarketingDistribuitionChannelMapper INSTANCE = Mappers.getMapper(MarketingDistribuitionChannelMapper.class);

    @Mappings({
        // @Mapping(target = "codCanal", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    MarketingDistribuitionChannel toDomain(MarketingDistribuitionChannelEntityJpa entity);

    @Mappings({
        // @Mapping(target = "codCanal", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    MarketingDistribuitionChannelEntityJpa toEntity(MarketingDistribuitionChannel domain);

    MarketingDistribuitionChannelKey toDomainKey(MarketingDistribuitionChannelKeyEntityJpa entityKey);

    MarketingDistribuitionChannelKeyEntityJpa toEntityKey(MarketingDistribuitionChannelKey domainKey);

    default ChannelDomainStatus stringToChannelDomainStatus(String code) {
        if (code == null || code.isBlank()) return null;
        return ChannelDomainStatus.fromCode(code);
    }

    default String channelDomainStatusToString(ChannelDomainStatus status) {
        if (status == null) return null;
        return status.getCode();
    }
}
