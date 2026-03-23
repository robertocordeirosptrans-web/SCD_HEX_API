package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.adapter.out.jpa.entity.FeeEntityJpa;

public interface FeeMapper {

	static Fee toDomain(FeeEntityJpa entity) {
		if (entity == null) return null;
		Fee fee = new Fee();
		fee.setCodTaxa(entity.getCodTaxa());
		fee.setDtInicial(entity.getDtInicial());
		fee.setDscTaxa(entity.getDscTaxa());
		fee.setDtFinal(entity.getDtFinal());
		fee.setCodCanal(entity.getCodCanal());
		fee.setCodProduto(entity.getCodProduto());
		// Relacionamentos: canal, produto, taxas* não mapeados aqui
		return fee;
	}

	static FeeEntityJpa toEntity(Fee fee) {
		if (fee == null) return null;
		FeeEntityJpa entity = new FeeEntityJpa();
		entity.setCodTaxa(fee.getCodTaxa());
		entity.setDtInicial(fee.getDtInicial());
		entity.setDscTaxa(fee.getDscTaxa());
		entity.setDtFinal(fee.getDtFinal());
		entity.setCodCanal(fee.getCodCanal());
		entity.setCodProduto(fee.getCodProduto());
		return entity;
	}
}
