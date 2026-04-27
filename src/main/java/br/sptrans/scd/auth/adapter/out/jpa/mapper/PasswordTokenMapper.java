package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.PasswordTokenEntityJpa;
import br.sptrans.scd.auth.domain.PasswordResetToken;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PasswordTokenMapper {

    @Mapping(target = "usado", source = "used")
    PasswordResetToken toDomain(PasswordTokenEntityJpa entity);

    @Mapping(target = "used", source = "usado")
    PasswordTokenEntityJpa toEntity(PasswordResetToken domain);
}
