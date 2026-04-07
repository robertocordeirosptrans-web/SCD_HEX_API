package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.auth.adapter.out.persistence.entity.UserEntityJpa;
import br.sptrans.scd.auth.domain.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityJpaMapper {

    default User toDomain(UserEntityJpa entity) {
        if (entity == null) return null;
        User user = new User();
        user.setIdUsuario(entity.getIdUsuario());
        user.setCodLogin(entity.getCodLogin());
        user.setNomUsuario(entity.getNomUsuario());
        return user;
    }

    default UserEntityJpa toEntity(User user) {
        if (user == null) return null;
        UserEntityJpa entity = new UserEntityJpa();
        entity.setIdUsuario(user.getIdUsuario());
        return entity;
    }

    default UserEntityJpa toEntity(Long userId) {
        if (userId == null) return null;
        UserEntityJpa entity = new UserEntityJpa();
        entity.setIdUsuario(userId);
        return entity;
    }

    default Long toUserId(UserEntityJpa entity) {
        if (entity == null) return null;
        return entity.getIdUsuario();
    }
}
