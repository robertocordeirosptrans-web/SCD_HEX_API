package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.ContactChannelEntityJpa;

@Mapper(componentModel = "spring")
public interface ContactChannelMapper {
    ContactChannelMapper INSTANCE = Mappers.getMapper(ContactChannelMapper.class);

    @Mapping(target = "codContato", source = "codContato")
    // Adicione outros mapeamentos conforme necessário
    ContactChannel toDomain(ContactChannelEntityJpa entity);

    @Mapping(target = "codContato", source = "codContato")
    // Adicione outros mapeamentos conforme necessário
    ContactChannelEntityJpa toEntity(ContactChannel domain);
}
