package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.SalesChannelResponseDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.SalesChannelEntityJpa;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.channel.domain.enums.ChannelDomainStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesChannelMapper {

    // Mapeamento de domínio para DTO de resposta
    @Mapping(source = "codAtividade.desAtividade", target = "desAtividade")
    @Mapping(source = "codAtividade.codAtividade", target = "codAtividade")
    @Mapping(source = "codClassificacaoPessoa.codClassificacaoPessoa", target = "codClassificacaoPessoa")
    @Mapping(source = "codClassificacaoPessoa.desClassificacaoPessoa", target = "desClassificacaoPessoa")
    @Mapping(source = "idUsuarioCadastro.codLogin", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao.codLogin", target = "usuarioManutencao")
    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastroInfo")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencaoInfo")
    SalesChannelResponseDTO toResponseDTO(SalesChannel channel);

    // Métodos para persistência (pode ignorar campos complexos, como no exemplo)
    @Mappings({
        @Mapping(target = "codClassificacaoPessoa", ignore = true),
        @Mapping(target = "codAtividade", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    SalesChannel toDomain(SalesChannelEntityJpa entity);

    @Mappings({
        @Mapping(source = "codClassificacaoPessoa.codClassificacaoPessoa", target = "codClassificacaoPessoa"),
        @Mapping(source = "codAtividade.codAtividade", target = "codAtividade"),
        @Mapping(source = "idUsuarioCadastro.idUsuario", target = "idUsuarioCadastro"),
        @Mapping(source = "idUsuarioManutencao.idUsuario", target = "idUsuarioManutencao")
    })
    SalesChannelEntityJpa toEntity(SalesChannel domain);

    @Mappings({
        @Mapping(source = "codClassificacaoPessoa.codClassificacaoPessoa", target = "codClassificacaoPessoa"),
        @Mapping(source = "codAtividade.codAtividade", target = "codAtividade"),
        @Mapping(source = "idUsuarioCadastro.idUsuario", target = "idUsuarioCadastro"),
        @Mapping(source = "idUsuarioManutencao.idUsuario", target = "idUsuarioManutencao")
    })
    void updateFromDomain(SalesChannel domain, @MappingTarget SalesChannelEntityJpa entity);

    // Exemplo de método auxiliar para User -> UserSimpleDTO
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
