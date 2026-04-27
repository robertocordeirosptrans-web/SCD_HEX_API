package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductTypesEntityJpa;
import br.sptrans.scd.product.domain.ProductType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface ProductsTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
    @Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
    ProductType toDomain(ProductTypesEntityJpa entity);

    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    ProductTypesEntityJpa toEntity(ProductType productType);
}
