package br.sptrans.scd.product.adapter.out.jpa.mapper;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.out.jpa.entity.SpeciesEntityJpa;
import br.sptrans.scd.product.domain.Species;

public interface SpeciesMapper {

	static Species toDomain(SpeciesEntityJpa entity, UserRepository userRepository) {
		if (entity == null) return null;
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
				dtCadastro,
				dtManutencao,
				usuarioCadastro,
				usuarioManutencao
		);
	}

	static SpeciesEntityJpa toEntity(Species domain) {
		if (domain == null) return null;
		String dtCadastro = domain.getDtCadastro() != null ? domain.getDtCadastro().toString() : null;
		String dtManutencao = domain.getDtManutencao() != null ? domain.getDtManutencao().toString() : null;
		SpeciesEntityJpa entity = new SpeciesEntityJpa();
		entity.setCodEspecie(domain.getCodEspecie());
		entity.setDesEspecie(domain.getDesEspecie());
		entity.setCodStatus(domain.getCodStatus());
		entity.setDtCadastro(dtCadastro);
		entity.setDtManutencao(dtManutencao);
		if (domain.getIdUsuarioCadastro() != null) {
			entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro().getIdUsuario());
		}
		if (domain.getIdUsuarioManutencao() != null) {
			entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao().getIdUsuario());
		}
		return entity;
	}
}
