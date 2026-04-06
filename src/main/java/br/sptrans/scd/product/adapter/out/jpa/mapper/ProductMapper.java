package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.domain.Product;

public interface ProductMapper {
	static Product toDomain(ProductEntityJpa entity) {
		if (entity == null) return null;
		Product p = new Product();
		p.setCodProduto(entity.getCodProduto());
		p.setDesProduto(entity.getDesProduto());
		p.setDesEmissorResponsavel(entity.getDesEmissorResponsavel());
		p.setCodStatus(entity.getCodStatus());
		p.setDesUtilizacao(entity.getDesUtilizacao());
		p.setDtCadastro(entity.getDtCadastro());
		p.setDtManutencao(entity.getDtManutencao());
		p.setFlgBloqFabricacao(entity.getFlgBloqFabricacao());
		p.setFlgBloqVenda(entity.getFlgBloqVenda());
		p.setFlgBloqDistribuicao(entity.getFlgBloqDistribuicao());
		p.setFlgBloqTroca(entity.getFlgBloqTroca());
		p.setFlgBloqAquisicao(entity.getFlgBloqAquisicao());
		p.setFlgBloqPedido(entity.getFlgBloqPedido());
		p.setFlgBloqDevolucao(entity.getFlgBloqDevolucao());
		p.setFlgInicializado(entity.getFlgInicializado());
		p.setFlgComercializado(entity.getFlgComercializado());
		p.setFlgRestManual(entity.getFlgRestManual());
		p.setCodEntidade(entity.getCodEntidade());
		p.setCodTipoCartao(entity.getCodTipoCartao());
		p.setCodClassificacaoPessoa(entity.getCodClassificacaoPessoa());
		p.setCodTipoProduto(entity.getCodTipoProduto());
		p.setCodTecnologia(entity.getCodTecnologia());
		p.setCodModalidade(entity.getCodModalidade());
		p.setCodFamilia(entity.getCodFamilia());
		p.setCodEspecie(entity.getCodEspecie());
		p.setIdUsuarioCadastro(entity.getIdUsuarioCadastro());
		p.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
		return p;
	}

	static ProductEntityJpa toEntity(Product p) {
		if (p == null) return null;
		ProductEntityJpa entity = new ProductEntityJpa();
		entity.setCodProduto(p.getCodProduto());
		entity.setDesProduto(p.getDesProduto());
		entity.setDesEmissorResponsavel(p.getDesEmissorResponsavel());
		entity.setCodStatus(p.getCodStatus());
		entity.setDesUtilizacao(p.getDesUtilizacao());
		entity.setDtCadastro(p.getDtCadastro());
		entity.setDtManutencao(p.getDtManutencao());
		entity.setFlgBloqFabricacao(p.getFlgBloqFabricacao());
		entity.setFlgBloqVenda(p.getFlgBloqVenda());
		entity.setFlgBloqDistribuicao(p.getFlgBloqDistribuicao());
		entity.setFlgBloqTroca(p.getFlgBloqTroca());
		entity.setFlgBloqAquisicao(p.getFlgBloqAquisicao());
		entity.setFlgBloqPedido(p.getFlgBloqPedido());
		entity.setFlgBloqDevolucao(p.getFlgBloqDevolucao());
		entity.setFlgInicializado(p.getFlgInicializado());
		entity.setFlgComercializado(p.getFlgComercializado());
		entity.setFlgRestManual(p.getFlgRestManual());
		entity.setCodEntidade(p.getCodEntidade());
		entity.setCodTipoCartao(p.getCodTipoCartao());
		entity.setCodClassificacaoPessoa(p.getCodClassificacaoPessoa());
		entity.setCodTipoProduto(p.getCodTipoProduto());
		entity.setCodTecnologia(p.getCodTecnologia());
		entity.setCodModalidade(p.getCodModalidade());
		entity.setCodFamilia(p.getCodFamilia());
		entity.setCodEspecie(p.getCodEspecie());
		entity.setIdUsuarioCadastro(p.getIdUsuarioCadastro());
		entity.setIdUsuarioManutencao(p.getIdUsuarioManutencao());
		return entity;
	}
}