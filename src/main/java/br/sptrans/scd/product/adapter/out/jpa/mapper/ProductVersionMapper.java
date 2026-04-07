package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductVersionEntityJpa;
import br.sptrans.scd.product.domain.ProductVersion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface ProductVersionMapper {

	@Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
	@Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
	ProductVersion toDomain(ProductVersionEntityJpa entity);

	@Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
	@Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
	ProductVersionEntityJpa toEntity(ProductVersion version);
}
