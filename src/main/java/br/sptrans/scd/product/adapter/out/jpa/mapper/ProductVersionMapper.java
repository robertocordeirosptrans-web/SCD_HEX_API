package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.ProductVersionEntityJpa;
import br.sptrans.scd.product.domain.ProductVersion;

public interface ProductVersionMapper {
	static ProductVersion toDomain(ProductVersionEntityJpa entity) {
		if (entity == null) return null;
		ProductVersion v = new ProductVersion();
		v.setCodVersao(entity.getCodVersao());
		v.setCodProduto(entity.getCodProduto());
		v.setDtValidade(entity.getDtValidade());
		v.setDtVidaInicio(entity.getDtVidaInicio());
		v.setDtVidaFim(entity.getDtVidaFim());
		v.setDtLiberacao(entity.getDtLiberacao());
		v.setDtLancamento(entity.getDtLancamento());
		v.setDtVendaInicio(entity.getDtVendaInicio());
		v.setDtVendaFim(entity.getDtVendaFim());
		v.setDtUsoInicio(entity.getDtUsoInicio());
		v.setDtUsoFim(entity.getDtUsoFim());
		v.setDtTrocaInicio(entity.getDtTrocaInicio());
		v.setDtTrocaFim(entity.getDtTrocaFim());
		v.setFlgBloqFabricacao(entity.getFlgBloqFabricacao());
		v.setFlgBloqVenda(entity.getFlgBloqVenda());
		v.setFlgBloqDistribuicao(entity.getFlgBloqDistribuicao());
		v.setFlgBloqTroca(entity.getFlgBloqTroca());
		v.setFlgBloqAquisicao(entity.getFlgBloqAquisicao());
		v.setFlgBloqPedido(entity.getFlgBloqPedido());
		v.setFlgBloqDevolucao(entity.getFlgBloqDevolucao());
		v.setDtCadastro(entity.getDtCadastro());
		v.setDtManutencao(entity.getDtManutencao());
		v.setCodStatus(entity.getCodStatus());
		v.setDesProdutoVersoes(entity.getDesProdutoVersoes());
		v.setIdUsuarioCadastro(null); // Conversão de Long para User se necessário
		v.setIdUsuarioManutencao(null); // Conversão de Long para User se necessário
		return v;
	}

	static ProductVersionEntityJpa toEntity(ProductVersion v) {
		if (v == null) return null;
		ProductVersionEntityJpa entity = new ProductVersionEntityJpa();
		entity.setCodVersao(v.getCodVersao());
		entity.setCodProduto(v.getCodProduto());
		entity.setDtValidade(v.getDtValidade());
		entity.setDtVidaInicio(v.getDtVidaInicio());
		entity.setDtVidaFim(v.getDtVidaFim());
		entity.setDtLiberacao(v.getDtLiberacao());
		entity.setDtLancamento(v.getDtLancamento());
		entity.setDtVendaInicio(v.getDtVendaInicio());
		entity.setDtVendaFim(v.getDtVendaFim());
		entity.setDtUsoInicio(v.getDtUsoInicio());
		entity.setDtUsoFim(v.getDtUsoFim());
		entity.setDtTrocaInicio(v.getDtTrocaInicio());
		entity.setDtTrocaFim(v.getDtTrocaFim());
		entity.setFlgBloqFabricacao(v.getFlgBloqFabricacao());
		entity.setFlgBloqVenda(v.getFlgBloqVenda());
		entity.setFlgBloqDistribuicao(v.getFlgBloqDistribuicao());
		entity.setFlgBloqTroca(v.getFlgBloqTroca());
		entity.setFlgBloqAquisicao(v.getFlgBloqAquisicao());
		entity.setFlgBloqPedido(v.getFlgBloqPedido());
		entity.setFlgBloqDevolucao(v.getFlgBloqDevolucao());
		entity.setDtCadastro(v.getDtCadastro());
		entity.setDtManutencao(v.getDtManutencao());
		entity.setCodStatus(v.getCodStatus());
		entity.setDesProdutoVersoes(v.getDesProdutoVersoes());
		entity.setIdUsuarioCadastro(null); // Conversão de User para Long se necessário
		entity.setIdUsuarioManutencao(null); // Conversão de User para Long se necessário
		return entity;
	}
}
