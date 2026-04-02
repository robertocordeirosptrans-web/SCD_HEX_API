package br.sptrans.scd.product.adapter.out.jpa.mapper;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.port.out.persistence.entity.SpeciesEntityJpa;
import br.sptrans.scd.product.domain.Species;

public interface SpeciesMapper {

	static Species toDomain(SpeciesEntityJpa entity, UserPersistencePort userRepository) {
		if (entity == null) return null;
		
		User usuarioCadastro = null;
		User usuarioManutencao = null;
		if (entity.getIdUsuarioCadastro() != null) {
			usuarioCadastro = userRepository.findById(entity.getIdUsuarioCadastro()).orElse(null);
		}
		if (entity.getIdUsuarioManutencao() != null) {
			usuarioManutencao = userRepository.findById(entity.getIdUsuarioManutencao()).orElse(null);
		}
		return new Species(
				entity.getCodEspecie(),
				entity.getDesEspecie(),
				entity.getCodStatus(),
				entity.getDtCadastro() != null ? entity.getDtCadastro() : LocalDateTime.now(),
					entity.getDtManutencao() != null ? entity.getDtCadastro() : LocalDateTime.now(),
				usuarioCadastro,
				usuarioManutencao
		);
	}

	static SpeciesEntityJpa toEntity(Species domain) {
		if (domain == null) return null;
	
		SpeciesEntityJpa entity = new SpeciesEntityJpa();
		entity.setCodEspecie(domain.getCodEspecie());
		entity.setDesEspecie(domain.getDesEspecie());
		entity.setCodStatus(domain.getCodStatus());
		entity.setDtCadastro(domain.getDtCadastro());
		entity.setDtManutencao(domain.getDtManutencao());
		if (domain.getIdUsuarioCadastro() != null) {
			entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro().getIdUsuario());
		}
		if (domain.getIdUsuarioManutencao() != null) {
			entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao().getIdUsuario());
		}
		return entity;
	}
}
