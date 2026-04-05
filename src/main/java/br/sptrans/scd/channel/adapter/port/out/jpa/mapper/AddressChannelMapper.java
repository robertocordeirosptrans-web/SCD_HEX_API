package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AddressChannelEntityJpa;
import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.channel.domain.SalesChannel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressChannelMapper {

    @Mapping(source = "codCanal.codCanal", target = "codCanal")
    @Mapping(source = "idUsuarioCadastro.idUsuario", target = "idUsuarioCadastro")
    @Mapping(source = "idUsuarioManutencao.idUsuario", target = "idUsuarioManutencao")
    AddressChannelEntityJpa toEntity(AddressChannel domain);

    @Mapping(source = "entity.stEnderecos", target = "stEnderecos")
    @Mapping(source = "entity.dtManutencao", target = "dtManutencao")
    @Mapping(source = "entity.dtCadastro", target = "dtCadastro")
    @Mapping(source = "salesChannel", target = "codCanal")
    @Mapping(source = "userCad", target = "idUsuarioCadastro")
    @Mapping(source = "userMan", target = "idUsuarioManutencao")
    AddressChannel toDomain(AddressChannelEntityJpa entity, SalesChannel salesChannel, User userCad, User userMan);
}
