package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.RechargeLimitEntityJpa;
import br.sptrans.scd.channel.domain.RechargeLimit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RechargeLimitMapper {

    @Mappings({
        @Mapping(target = "idUsuarioCadastro", ignore = true),
    })
    RechargeLimit toDomain(RechargeLimitEntityJpa entity);

    @Mappings({
        @Mapping(target = "idUsuarioCadastro", ignore = true)})
    RechargeLimitEntityJpa toEntity(RechargeLimit domain);


}
