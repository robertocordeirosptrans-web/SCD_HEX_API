package br.sptrans.scd.auth.adapter.port.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.domain.Functionality;

public class FunctionalityMapper {
	public static Functionality toDomain(FunctionalityEntityJpa entity) {
		if (entity == null) return null;
		Functionality functionality = new Functionality();
		functionality.setCodSistema(entity.getId() != null ? entity.getId().getCodSistema() : null);
		functionality.setCodModulo(entity.getId() != null ? entity.getId().getCodModulo() : null);
		functionality.setCodRotina(entity.getId() != null ? entity.getId().getCodRotina() : null);
		functionality.setCodFuncionalidade(entity.getId() != null ? entity.getId().getCodFuncionalidade() : null);
		functionality.setNomFuncionalidade(entity.getNomFuncionalidade());
		functionality.setCodStatus(entity.getCodStatus());
		// Os campos dtCadastro e dtManutencao não existem em Functionality
		return functionality;
	}
}
