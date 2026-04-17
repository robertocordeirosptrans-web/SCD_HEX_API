package br.sptrans.scd.shared.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.Technology;

@Mapper(componentModel = "spring")
public interface CatalogueMapper {
    CatalogueMapper INSTANCE = Mappers.getMapper(CatalogueMapper.class);

    @Mapping(target = "codigo", source = "codTecnologia")
    @Mapping(target = "descricao", source = "desTecnologia")
    @Mapping(target = "codStatus", source = "codStatus")
    @Mapping(target = "dtCadastro", source = "dtCadastro")
    @Mapping(target = "dtManutencao", source = "dtManutencao")
    @Mapping(target = "usuarioCadastro", expression = "java(mapUser(entity.getIdUsuarioCadastro()))")
    @Mapping(target = "usuarioManutencao", expression = "java(mapUser(entity.getIdUsuarioManutencao()))")
    CatalogueDTO toDto(Technology entity);

    @Mapping(target = "codigo", source = "codModalidade")
    @Mapping(target = "descricao", source = "desModalidade")
    @Mapping(target = "codStatus", source = "codStatus")
    @Mapping(target = "dtCadastro", source = "dtCadastro")
    @Mapping(target = "dtManutencao", source = "dtManutencao")
    @Mapping(target = "usuarioCadastro", expression = "java(mapUser(entity.getIdUsuarioCadastro()))")
    @Mapping(target = "usuarioManutencao", expression = "java(mapUser(entity.getIdUsuarioManutencao()))")
    CatalogueDTO toDto(Modality entity);

    @Mapping(target = "codigo", source = "codFamilia")
    @Mapping(target = "descricao", source = "desFamilia")
    @Mapping(target = "codStatus", source = "codStatus")
    @Mapping(target = "dtCadastro", source = "dtCadastro")
    @Mapping(target = "dtManutencao", source = "dtManutencao")
    @Mapping(target = "usuarioCadastro", expression = "java(mapUser(entity.getIdUsuarioCadastro()))")
    @Mapping(target = "usuarioManutencao", expression = "java(mapUser(entity.getIdUsuarioManutencao()))")
    CatalogueDTO toDto(Family entity);

    @Mapping(target = "codigo", source = "codEspecie")
    @Mapping(target = "descricao", source = "desEspecie")
    @Mapping(target = "codStatus", source = "codStatus")
    @Mapping(target = "dtCadastro", source = "dtCadastro")
    @Mapping(target = "dtManutencao", source = "dtManutencao")
    @Mapping(target = "usuarioCadastro", expression = "java(mapUser(entity.getIdUsuarioCadastro()))")
    @Mapping(target = "usuarioManutencao", expression = "java(mapUser(entity.getIdUsuarioManutencao()))")
    CatalogueDTO toDto(Species entity);

    @Mapping(target = "codigo", source = "codTipoProduto")
    @Mapping(target = "descricao", source = "desTipoProduto")
    @Mapping(target = "codStatus", source = "codStatus")
    @Mapping(target = "dtCadastro", source = "dtCadastro")
    @Mapping(target = "dtManutencao", source = "dtManutencao")
    @Mapping(target = "usuarioCadastro", expression = "java(mapUser(entity.getIdUsuarioCadastro()))")
    @Mapping(target = "usuarioManutencao", expression = "java(mapUser(entity.getIdUsuarioManutencao()))")
    CatalogueDTO toDto(ProductType entity);

    default br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO mapUser(br.sptrans.scd.auth.domain.User user) {
        if (user == null) return null;
        return new br.sptrans.scd.channel.adapter.in.rest.dto.UserSimpleDTO(user.getIdUsuario(), user.getCodLogin(), user.getNomUsuario());
    }
}
