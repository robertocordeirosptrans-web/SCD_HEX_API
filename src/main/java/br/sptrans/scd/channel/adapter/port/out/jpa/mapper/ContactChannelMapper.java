





package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.ContactChannelEntityJpa;
import br.sptrans.scd.channel.domain.ContactChannel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactChannelMapper {


    // Mapeamento direto dos campos, sem propriedades inexistentes
    @Mappings({
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true),
        @Mapping(target = "codCanal", ignore = true)
    })
    ContactChannel toDomain(ContactChannelEntityJpa entity);

    /**
     * Converte ContactChannel para entidade ContactChannelEntityJpa (novo)
     */
    @Mappings({
        @Mapping(target = "codCanal", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    ContactChannelEntityJpa toEntity(ContactChannel domain);

    /**
     * Atualiza uma entidade ContactChannelEntityJpa existente a partir de ContactChannel
     */
    @Mappings({
        @Mapping(target = "codCanal", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true),
        @Mapping(target = "codContato", ignore = true)
    })
    void updateFromDomain(ContactChannel domain, @MappingTarget ContactChannelEntityJpa entity);
}
