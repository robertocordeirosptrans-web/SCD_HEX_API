package br.sptrans.scd.auth.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.in.rest.dto.GroupProfileResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.auth.domain.GroupProfile;

@Component
public class GroupProfileRestMapper {

    public GroupProfileResponseDTO toDto(GroupProfile gp) {
        if (gp == null) return null;
        return new GroupProfileResponseDTO(
                gp.getId() != null ? gp.getId().getCodGrupo() : null,
                gp.getId() != null ? gp.getId().getCodPerfil() : null,
                gp.getCodStatus(),
                gp.getDtModi(),
                UserSimpleDTO.from(gp.getUsuarioManutencao()));
    }
}
