package br.sptrans.scd.channel.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.ContactChannelResponseDTO;
import br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.channel.adapter.out.persistence.entity.ContactChannelEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.SalesChannelEntityJpa;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.channel.domain.SalesChannel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactChannelMapper {

    // Mapeamento direto dos campos com conversão de entidades relacionadas
    @Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
    @Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
    @Mapping(source = "canal", target = "codCanal")
    ContactChannel toDomain(ContactChannelEntityJpa entity);

    /**
     * Converte ContactChannel para entidade ContactChannelEntityJpa (novo)
     */
    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    @Mapping(source = "codCanal", target = "canal")
    ContactChannelEntityJpa toEntity(ContactChannel domain);

    /**
     * Atualiza uma entidade ContactChannelEntityJpa existente a partir de
     * ContactChannel
     */
    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    @Mapping(source = "codCanal", target = "canal")
    @Mapping(target = "codContato", ignore = true)
    void updateFromDomain(ContactChannel domain, @MappingTarget ContactChannelEntityJpa entity);

    // Métodos auxiliares para conversão entre entidades JPA e domínio
    default User map(UserEntityJpa entity) {
        if (entity == null)
            return null;
        User user = new User();
        user.setIdUsuario(entity.getIdUsuario());
        return user;
    }

    default UserEntityJpa map(User domain) {
        if (domain == null)
            return null;
        UserEntityJpa entity = new UserEntityJpa();
        entity.setIdUsuario(domain.getIdUsuario());
        return entity;
    }

    default SalesChannel map(SalesChannelEntityJpa entity) {
        if (entity == null)
            return null;
        SalesChannel salesChannel = new SalesChannel();
        salesChannel.setCodCanal(entity.getCodCanal());
        salesChannel.setDesCanal(entity.getDesCanal());
        return salesChannel;
    }

    default SalesChannelEntityJpa map(SalesChannel domain) {
        if (domain == null)
            return null;
        SalesChannelEntityJpa entity = new SalesChannelEntityJpa();
        entity.setCodCanal(domain.getCodCanal());
        entity.setDesCanal(domain.getDesCanal());
        return entity;
    }

    @Mapping(source = "codCanal.codCanal", target = "codCanal")
    @Mapping(source = "codCanal.desCanal", target = "desCanal")
    @Mapping(source = "idUsuarioCadastro.codLogin", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao.codLogin", target = "usuarioManutencao")
    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastroInfo")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencaoInfo")
    ContactChannelResponseDTO toResponseDTO(ContactChannel domain);

    default UserSimpleDTO mapUserToUserSimpleDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserSimpleDTO(
                user.getIdUsuario(),
                user.getCodLogin(),
                user.getNomUsuario());
    }
}
