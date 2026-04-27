package br.sptrans.scd.auth.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.auth.domain.Profile;

@Component
public class ProfileRestMapper {

    public ProfileResponseDTO toDto(Profile profile) {
        if (profile == null) return null;
        return new ProfileResponseDTO(
                profile.getCodPerfil(),
                profile.getNomPerfil(),
                profile.getCodStatus(),
                profile.getDtModi(),
                UserSimpleDTO.from(profile.getUsuarioManutencao()));
    }
}
