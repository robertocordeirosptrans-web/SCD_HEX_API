package br.sptrans.scd.auth.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.in.rest.dto.GrupoResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.auth.domain.Group;

@Component
public class GroupRestMapper {

    public GrupoResponseDTO toDto(Group group) {
        if (group == null) return null;
        return new GrupoResponseDTO(
                group.getCodGrupo(),
                group.getNomGrupo(),
                group.getCodStatus(),
                group.getDtModi(),
                UserSimpleDTO.from(group.getUsuarioManutencao()));
    }
}
