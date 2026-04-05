package br.sptrans.scd.auth.adapter.port.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.ProfileEntityJpa;
import br.sptrans.scd.auth.domain.Profile;

public class ProfileMapper {
	public static Profile toDomain(ProfileEntityJpa entity) {
		if (entity == null) return null;
		Profile profile = new Profile();
		profile.setCodPerfil(entity.getCodPerfil());
		profile.setNomPerfil(entity.getNomPerfil());
		profile.setCodStatus(entity.getCodStatus());
		profile.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
		profile.setDtModi(entity.getDtManutencao());
		return profile;
	}
}
