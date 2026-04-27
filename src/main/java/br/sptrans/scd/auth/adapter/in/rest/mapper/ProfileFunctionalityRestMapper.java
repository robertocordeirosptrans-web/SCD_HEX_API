package br.sptrans.scd.auth.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.in.rest.dto.ProfileFunctionalityResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.auth.domain.ProfileFunctionality;

@Component
public class ProfileFunctionalityRestMapper {

    public ProfileFunctionalityResponseDTO toDto(ProfileFunctionality pf) {
        if (pf == null) return null;
        return new ProfileFunctionalityResponseDTO(
                pf.getId().getCodSistema(),
                pf.getId().getCodModulo(),
                pf.getId().getCodRotina(),
                pf.getId().getCodFuncionalidade(),
                pf.getId().getCodPerfil(),
                pf.getDtInicioValidade(),
                UserSimpleDTO.from(pf.getUsuarioManutencao()));
    }
}
