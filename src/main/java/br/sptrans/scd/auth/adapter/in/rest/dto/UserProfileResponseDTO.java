package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.UserProfile;

public record UserProfileResponseDTO(
    Long idUsuario,
    String codPerfil,
    String nomPerfil,
    Long idUsuarioManutencao,
    String codStatus,
    LocalDateTime dtModi
) {
    public UserProfileResponseDTO(UserProfile usuarioPerfil) {
        this(
            (usuarioPerfil != null && usuarioPerfil.getId() != null && usuarioPerfil.getId().getIdUsuario() != null)
                ? usuarioPerfil.getId().getIdUsuario() : null,
            (usuarioPerfil != null && usuarioPerfil.getId() != null && usuarioPerfil.getId().getCodPerfil() != null)
                ? usuarioPerfil.getId().getCodPerfil() : null,
            (usuarioPerfil != null && usuarioPerfil.getPerfil() != null && usuarioPerfil.getPerfil().getNomPerfil() != null)
                ? usuarioPerfil.getPerfil().getNomPerfil() : null,
            usuarioPerfil != null ? usuarioPerfil.getIdUsuarioManutencao() : null,
            usuarioPerfil != null ? usuarioPerfil.getCodStatus() : null,
            usuarioPerfil != null ? usuarioPerfil.getDtModi() : null
        );
    }
}
