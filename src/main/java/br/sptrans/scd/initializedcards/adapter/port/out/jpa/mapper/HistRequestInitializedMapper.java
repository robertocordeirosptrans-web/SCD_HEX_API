package br.sptrans.scd.initializedcards.adapter.port.out.jpa.mapper;

import br.sptrans.scd.initializedcards.adapter.port.out.persistence.entity.HistRICEntityJpa;
import br.sptrans.scd.initializedcards.domain.HistRequestInitializedCards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistRequestInitializedMapper {

    @Mappings({
        @Mapping(target = "codCanal", ignore = true),
        @Mapping(target = "codProduto", ignore = true),
        @Mapping(target = "idUsuarioAprovacao", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    HistRequestInitializedCards toDomain(HistRICEntityJpa entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "codProduto", ignore = true),
        @Mapping(target = "idUsuarioAprovacao", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    HistRICEntityJpa toEntity(HistRequestInitializedCards domain);
}
