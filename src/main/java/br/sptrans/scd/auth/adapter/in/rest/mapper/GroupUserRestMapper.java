package br.sptrans.scd.auth.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.adapter.in.rest.dto.GroupUserResponseDTO;
import br.sptrans.scd.auth.adapter.in.rest.dto.UserSimpleDTO;
import br.sptrans.scd.auth.domain.GroupUser;

@Component
public class GroupUserRestMapper {

    public GroupUserResponseDTO toDto(GroupUser gu) {
        if (gu == null) return null;
        return new GroupUserResponseDTO(
                gu.getId() != null ? gu.getId().getIdUsuario() : null,
                gu.getId() != null ? gu.getId().getCodGrupo() : null,
                gu.getCodStatus(),
                gu.getDtModi(),
                UserSimpleDTO.from(gu.getUsuario()),
                UserSimpleDTO.from(gu.getUsuarioManutencao()));
    }
}
