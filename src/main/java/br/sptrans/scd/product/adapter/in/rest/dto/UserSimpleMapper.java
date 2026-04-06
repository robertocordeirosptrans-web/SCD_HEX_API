package br.sptrans.scd.product.adapter.in.rest.dto;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.in.rest.dto.UserSimpleDTO;


public class UserSimpleMapper {
    public static UserSimpleDTO toDto(User user) {
        if (user == null) return null;
        return new UserSimpleDTO(user.getIdUsuario(), user.getCodLogin(), user.getNomUsuario());
    }
}
