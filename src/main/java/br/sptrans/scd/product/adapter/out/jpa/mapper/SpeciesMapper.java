package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.adapter.out.jpa.entity.SpeciesEntityJpa;
import java.time.LocalDateTime;

public interface SpeciesMapper {

	static Species toDomain(SpeciesEntityJpa entity) {
		if (entity == null) return null;
		// Conversão simplificada: datas como String -> LocalDateTime (pode ser ajustado conforme formato real)
		LocalDateTime dtCadastro = null;
		LocalDateTime dtManutencao = null;
		try {
			if (entity.getDtCadastro() != null)
				dtCadastro = LocalDateTime.parse(entity.getDtCadastro());
			if (entity.getDtManutencao() != null)
				dtManutencao = LocalDateTime.parse(entity.getDtManutencao());
		} catch (Exception e) {
			// Ignorar parse error, manter null
		}
		return new Species(
				entity.getCodEspecie(),
				entity.getDesEspecie(),
				entity.getCodStatus(),
				dtCadastro,
				dtManutencao,
				null, // idUsuarioCadastro não mapeado
				null  // idUsuarioManutencao não mapeado
		);
	}

	static SpeciesEntityJpa toEntity(Species domain) {
		if (domain == null) return null;
		// Conversão simplificada: datas como LocalDateTime -> String (pode ser ajustado conforme formato real)
		String dtCadastro = domain.getDtCadastro() != null ? domain.getDtCadastro().toString() : null;
		String dtManutencao = domain.getDtManutencao() != null ? domain.getDtManutencao().toString() : null;
		return new SpeciesEntityJpa(
				domain.getCodEspecie(),
				domain.getDesEspecie(),
				domain.getCodStatus(),
				dtCadastro,
				dtManutencao
		);
	}
}
