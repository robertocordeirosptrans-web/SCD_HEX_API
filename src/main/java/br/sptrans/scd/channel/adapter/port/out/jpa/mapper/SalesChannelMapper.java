package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.in.rest.CanalResponseDTO;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.SalesChannelEntityJpa;
import br.sptrans.scd.channel.domain.SalesChannel;

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
    CanalResponseDTO toResponseDTO(SalesChannel channel);

    // Métodos para persistência (pode ignorar campos complexos, como no exemplo)
    @Mappings({
        @Mapping(target = "codClassificacaoPessoa", ignore = true),
        @Mapping(target = "codAtividade", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    SalesChannel toDomain(SalesChannelEntityJpa entity);

    @Mappings({
        @Mapping(target = "codClassificacaoPessoa", ignore = true),
        @Mapping(target = "codAtividade", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
    })
    SalesChannelEntityJpa toEntity(SalesChannel domain);

    @Mappings({
        @Mapping(target = "codClassificacaoPessoa", ignore = true),
        @Mapping(target = "codAtividade", ignore = true),
        @Mapping(target = "idUsuarioCadastro", ignore = true),
        @Mapping(target = "idUsuarioManutencao", ignore = true)
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

}
