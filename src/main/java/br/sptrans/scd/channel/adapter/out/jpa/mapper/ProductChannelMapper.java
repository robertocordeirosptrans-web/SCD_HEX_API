package br.sptrans.scd.channel.adapter.out.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.ProductChResponseDTO;
import br.sptrans.scd.channel.adapter.out.persistence.entity.ProductChannelEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.ProductChannelKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.query.ProductChannelProjection;
import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.ProductChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductChannelMapper {

    @Mapping(source = "idUsuarioCadastro.idUsuario", target = "idUsuarioCadastro")
    @Mapping(source = "idUsuarioManutencao.idUsuario", target = "idUsuarioManutencao")
    ProductChannelEntityJpa toEntity(ProductChannel domain);

    @Mapping(source = "entity.codStatus", target = "codStatus")
    @Mapping(source = "userCad", target = "idUsuarioCadastro")
    @Mapping(source = "userMan", target = "idUsuarioManutencao")
    ProductChannel toDomain(ProductChannelEntityJpa entity, User userCad, User userMan);

    ProductChannelKeyEntityJpa toEntityKey(ProductChannelKey key);

    ProductChannelKey toDomainKey(ProductChannelKeyEntityJpa entityKey);

    ProductChResponseDTO toResponseDTO(ProductChannelProjection projection);

    List<ProductChResponseDTO> toResponseDTOList(List<ProductChannelProjection> projections);

    default ChannelDomainStatus stringToChannelDomainStatus(String code) {
        if (code == null || code.isBlank()) return null;
        return ChannelDomainStatus.fromCode(code);
    }

    default String channelDomainStatusToString(ChannelDomainStatus status) {
        if (status == null) return null;
        return status.getCode();
    }
}
