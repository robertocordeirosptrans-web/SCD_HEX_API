package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = { FunctionalityKey.class })
public interface FunctionalityMapper {

    @Mapping(target = "id", expression = "java(entity.getId() != null ? new FunctionalityKey(entity.getId().getCodSistema(), entity.getId().getCodModulo(), entity.getId().getCodRotina(), entity.getId().getCodFuncionalidade()) : null)")
    @Mapping(target = "codSistema", source = "id.codSistema")
    @Mapping(target = "codModulo", source = "id.codModulo")
    @Mapping(target = "codRotina", source = "id.codRotina")
    @Mapping(target = "codFuncionalidade", source = "id.codFuncionalidade")
    @Mapping(target = "dtModi", source = "dtManutencao")
    @Mapping(target = "dtSinc", source = "dtCadastro")
    Functionality toDomain(FunctionalityEntityJpa entity);
}
