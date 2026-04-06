package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.out.persistence.entity.FeeEntityJpa;
import br.sptrans.scd.product.domain.Fee;

public interface FeeMapper {

	static Fee toDomain(FeeEntityJpa entity) {
		if (entity == null) return null;
		Fee fee = new Fee();
		fee.setCodTaxa(entity.getCodTaxa());
		fee.setDtInicio(entity.getDtInicio());
		fee.setDesTaxa(entity.getDesTaxa());
		fee.setDtFinal(entity.getDtFim());
		fee.setCodCanal(entity.getCodCanal());
		fee.setCodProduto(entity.getCodProduto());
		// Relacionamentos: canal, produto, taxas* não mapeados aqui
		return fee;
	}

	static FeeEntityJpa toEntity(Fee fee) {
		if (fee == null) return null;
		FeeEntityJpa entity = new FeeEntityJpa();
		entity.setCodTaxa(fee.getCodTaxa());
		entity.setDtInicio(fee.getDtInicio());
		entity.setDesTaxa(fee.getDesTaxa());
		entity.setDtFim(fee.getDtFinal());
		entity.setCodCanal(fee.getCodCanal());
		entity.setCodProduto(fee.getCodProduto());
		return entity;
	}
}
