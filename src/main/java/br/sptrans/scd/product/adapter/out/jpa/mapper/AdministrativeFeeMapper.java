package br.sptrans.scd.product.adapter.out.jpa.mapper;



import br.sptrans.scd.product.domain.AdministrativeFee;
import br.sptrans.scd.product.adapter.out.jpa.entity.AdministrativeFeeEntityJpa;

public interface AdministrativeFeeMapper {
	static AdministrativeFee toDomain(AdministrativeFeeEntityJpa entity) {
		if (entity == null) return null;
		return new AdministrativeFee(
			entity.getCodTaxaAdm(),
			entity.getRecInicial(),
			entity.getRecFinal(),
			entity.getValFixo(),
			entity.getValPercentual(),
			null // taxa (Fee) - relacionamento não mapeado
		);
	}

	static AdministrativeFeeEntityJpa toEntity(AdministrativeFee fee) {
		if (fee == null) return null;
		AdministrativeFeeEntityJpa entity = new AdministrativeFeeEntityJpa();
		entity.setCodTaxaAdm(fee.getCodTaxaAdm());
		entity.setRecInicial(fee.getRecInicial());
		entity.setRecFinal(fee.getRecFinal());
		entity.setValFixo(fee.getValFixo());
		entity.setValPercentual(fee.getValPercentual());
		return entity;
	}
}
