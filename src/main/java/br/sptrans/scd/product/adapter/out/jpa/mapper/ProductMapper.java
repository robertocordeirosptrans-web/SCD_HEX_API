package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.in.rest.dto.ProductResponseDTO;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.domain.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface ProductMapper {

	@Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
	@Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
	@Mapping(target = "desTipoProduto",   expression = "java(entity.getTipoProduto()  != null ? entity.getTipoProduto().getDesTipoProduto()  : null)")
	@Mapping(target = "desTecnologia",    expression = "java(entity.getTecnologia()   != null ? entity.getTecnologia().getDesTecnologia()    : null)")
	@Mapping(target = "desModalidade",    expression = "java(entity.getModalidade()   != null ? entity.getModalidade().getDesModalidade()    : null)")
	@Mapping(target = "desFamilia",       expression = "java(entity.getFamilia()      != null ? entity.getFamilia().getDesFamilia()          : null)")
	@Mapping(target = "desEspecie",       expression = "java(entity.getEspecie()      != null ? entity.getEspecie().getDesEspecie()          : null)")
	@Mapping(target = "nomeUsuarioCadastro",  expression = "java(entity.getUsuarioCadastro()  != null ? entity.getUsuarioCadastro().getNomUsuario()  : null)")
	@Mapping(target = "nomeUsuarioManutencao", expression = "java(entity.getUsuarioManutencao() != null ? entity.getUsuarioManutencao().getNomUsuario() : null)")
	Product toDomain(ProductEntityJpa entity);

	@Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
	@Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
	@Mapping(target = "tipoProduto",  ignore = true)
	@Mapping(target = "tecnologia",   ignore = true)
	@Mapping(target = "modalidade",   ignore = true)
	@Mapping(target = "familia",      ignore = true)
	@Mapping(target = "especie",      ignore = true)
	ProductEntityJpa toEntity(Product product);

	default ProductResponseDTO toResponseDTO(Product product) {
		if (product == null) return null;
		return new ProductResponseDTO(
			product.getCodProduto(),
			product.getCodTipoProduto(), product.getDesTipoProduto(),
			product.getCodTecnologia(),  product.getDesTecnologia(),
			product.getCodModalidade(),  product.getDesModalidade(),
			product.getIdUsuarioCadastro(), product.getNomeUsuarioCadastro(),
			product.getIdUsuarioManutencao(), product.getNomeUsuarioManutencao(),
			product.getCodFamilia(),  product.getDesFamilia(),
			product.getCodEspecie(),  product.getDesEspecie(),
			product.getDesProduto(),
			product.getDesEmissorResponsavel(),
			product.getCodStatus(),
			product.getDesUtilizacao(),
			product.getDtCadastro(),
			product.getDtManutencao(),
			product.getFlgBloqFabricacao(),
			product.getFlgBloqVenda(),
			product.getFlgBloqDistribuicao(),
			product.getFlgBloqTroca(),
			product.getFlgBloqAquisicao(),
			product.getFlgBloqPedido(),
			product.getFlgBloqDevolucao(),
			product.getFlgInicializado(),
			product.getFlgComercializado(),
			product.getCodEntidade()
		);
	}

	default ProductResponseDTO toResponseDTO(ProductEntityJpa entity) {
		if (entity == null) return null;
		return toResponseDTO(toDomain(entity));
	}
}