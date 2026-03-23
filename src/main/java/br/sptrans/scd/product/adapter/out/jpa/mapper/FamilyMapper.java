package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.adapter.out.jpa.entity.FamilyEntityJpa;

public interface FamilyMapper {

	static Family toDomain(FamilyEntityJpa entity) {
		if (entity == null) return null;
		return new Family(
			entity.getCodFamilia(),
			entity.getDesFamilia(),
			entity.getStFamilias(),
			entity.getDtCadastro(),
			entity.getDtManutencao(),
			null, // idUsuarioCadastro
			null  // idUsuarioManutencao
		);
	}

	static FamilyEntityJpa toEntity(Family family) {
		if (family == null) return null;
		FamilyEntityJpa entity = new FamilyEntityJpa();
		entity.setCodFamilia(family.getCodFamilia());
		entity.setDesFamilia(family.getDesFamilia());
		entity.setStFamilias(family.getStFamilias());
		entity.setDtCadastro(family.getDtCadastro());
		entity.setDtManutencao(family.getDtManutencao());
		// idUsuarioCadastro, idUsuarioManutencao: implementar se necessário
		return entity;
	}
}
