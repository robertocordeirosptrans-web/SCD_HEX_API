package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.channel.adapter.port.in.rest.dto.TypesActivityResponseDTO;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.TypesActivityEntityJpa;
import br.sptrans.scd.channel.domain.TypesActivity;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring")
public interface TypesActivityMapper {
    TypesActivityMapper INSTANCE = Mappers.getMapper(TypesActivityMapper.class);

    TypesActivity toDomain(TypesActivityEntityJpa entity);
    TypesActivityEntityJpa toEntity(TypesActivity domain);

    TypesActivityResponseDTO toResponseDTO(TypesActivity domain);

    default ChannelDomainStatus stringToChannelDomainStatus(String code) {
        if (code == null || code.isBlank()) return null;
        return ChannelDomainStatus.fromCode(code);
    }

    default String channelDomainStatusToString(ChannelDomainStatus status) {
        if (status == null) return null;
        return status.getCode();
    }
}
