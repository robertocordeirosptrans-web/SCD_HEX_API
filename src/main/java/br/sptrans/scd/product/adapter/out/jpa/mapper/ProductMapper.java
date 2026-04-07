package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.domain.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface ProductMapper {

	@Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
	@Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
	Product toDomain(ProductEntityJpa entity);

	@Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
	@Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
	ProductEntityJpa toEntity(Product product);
}