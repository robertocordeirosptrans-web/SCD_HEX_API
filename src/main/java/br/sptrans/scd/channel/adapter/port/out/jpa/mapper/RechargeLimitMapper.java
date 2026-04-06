package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.RechargeLimitResponseDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.RechargeLimitEntityJpa;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RechargeLimitMapper {

    @Mappings({
        @Mapping(target = "idUsuarioCadastro", ignore = true),
    })
    RechargeLimit toDomain(RechargeLimitEntityJpa entity);

    @Mappings({
        @Mapping(target = "idUsuarioCadastro", ignore = true)})
    RechargeLimitEntityJpa toEntity(RechargeLimit domain);

    @Mapping(source = "id.codCanal", target = "codCanal")
    @Mapping(source = "id.codProduto", target = "codProduto")
    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastroInfo")
    RechargeLimitResponseDTO toResponseDTO(RechargeLimit domain);

    default UserSimpleDTO mapUserToUserSimpleDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserSimpleDTO(
            user.getIdUsuario(),
            user.getCodLogin(),
            user.getNomUsuario()
        );
    }

    default ChannelDomainStatus stringToChannelDomainStatus(String code) {
        if (code == null || code.isBlank()) return null;
        return ChannelDomainStatus.fromCode(code);
    }

    default String channelDomainStatusToString(ChannelDomainStatus status) {
        if (status == null) return null;
        return status.getCode();
    }

}
