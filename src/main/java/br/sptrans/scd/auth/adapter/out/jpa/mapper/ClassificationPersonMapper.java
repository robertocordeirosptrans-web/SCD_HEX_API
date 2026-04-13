package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.ClassificationPersonEntity;
import br.sptrans.scd.auth.domain.ClassificationPerson;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassificationPersonMapper {

    @Mapping(target = "idUsuarioCadastro", ignore = true)
    @Mapping(target = "idUsuarioManutencao", ignore = true)
    ClassificationPerson toDomain(ClassificationPersonEntity entity);
}
