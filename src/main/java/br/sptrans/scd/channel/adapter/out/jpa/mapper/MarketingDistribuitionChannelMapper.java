package br.sptrans.scd.channel.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.MarketingDistribuitionChannelResponseDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.channel.adapter.out.persistence.entity.MarketingDistribuitionChannelEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.MarketingDistribuitionChannelKeyEntityJpa;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring")
public interface MarketingDistribuitionChannelMapper {

    MarketingDistribuitionChannelMapper INSTANCE = Mappers.getMapper(MarketingDistribuitionChannelMapper.class);

    /**
     * Mapeamento manual necessário: {@code id} e {@code dtCadastro} são campos {@code final}
     * sem setter em {@link MarketingDistribuitionChannel}, portanto o MapStruct gerado não
     * consegue preenchê-los. O construtor completo ({@code @AllArgsConstructor}) é usado.
     */
    default MarketingDistribuitionChannel toDomain(MarketingDistribuitionChannelEntityJpa entity) {
        if (entity == null) return null;
        return new MarketingDistribuitionChannel(
            toDomainKey(entity.getId()),
            stringToChannelDomainStatus(entity.getCodStatus()),
            entity.getDtCadastro(),
            entity.getDtManutencao(),
            null, // idUsuarioCadastro — ignorado
            null  // idUsuarioManutencao — ignorado
        );
    }

    @Mappings({
        // @Mapping(target = "codCanal", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    MarketingDistribuitionChannelEntityJpa toEntity(MarketingDistribuitionChannel domain);

    /**
     * Mapeamento manual: {@link MarketingDistribuitionChannelKey} tem campos {@code final}
     * sem setter — MapStruct não consegue populá-los via construtor sem args.
     */
    default MarketingDistribuitionChannelKey toDomainKey(MarketingDistribuitionChannelKeyEntityJpa entityKey) {
        if (entityKey == null) return null;
        return new MarketingDistribuitionChannelKey(
            entityKey.getCodCanalComercializacao(),
            entityKey.getCodCanalDistribuicao()
        );
    }

    MarketingDistribuitionChannelKeyEntityJpa toEntityKey(MarketingDistribuitionChannelKey domainKey);

    @Mapping(source = "id.codCanalComercializacao", target = "codCanalComercializacao")
    @Mapping(source = "id.codCanalDistribuicao", target = "codCanalDistribuicao")
    @Mapping(source = "idUsuarioCadastro.codLogin", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao.codLogin", target = "usuarioManutencao")
    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastroInfo")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencaoInfo")
    MarketingDistribuitionChannelResponseDTO toResponseDTO(MarketingDistribuitionChannel domain);

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
