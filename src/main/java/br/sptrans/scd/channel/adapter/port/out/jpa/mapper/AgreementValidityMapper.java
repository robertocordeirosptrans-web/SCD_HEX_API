package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AgreementValidityKeyEntityJpa;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgreementValidityMapper {

    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    AgreementValidityEntityJpa toEntity(AgreementValidity domain);

    @Mapping(target = "usuario", ignore = true)
    AgreementValidity toDomain(AgreementValidityEntityJpa entity);

    AgreementValidityKeyEntityJpa toEntityKey(AgreementValidityKey key);

    AgreementValidityKey toDomainKey(AgreementValidityKeyEntityJpa entityKey);

    default ChannelDomainStatus stringToChannelDomainStatus(String code) {
        if (code == null || code.isBlank()) return null;
        return ChannelDomainStatus.fromCode(code);
    }

    default String channelDomainStatusToString(ChannelDomainStatus status) {
        if (status == null) return null;
        return status.getCode();
    }
}
