package br.sptrans.scd.auth.adapter.in.rest.dto;

import br.sptrans.scd.auth.domain.User;

public record UserSimpleDTO(Long idUsuario, String codLogin, String nomUsuario) {

    public static UserSimpleDTO from(User user) {
        if (user == null) return null;
        return new UserSimpleDTO(user.getIdUsuario(), user.getCodLogin(), user.getNomUsuario());
    }
}
