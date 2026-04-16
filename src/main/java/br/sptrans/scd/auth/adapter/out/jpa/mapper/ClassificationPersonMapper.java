package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

import br.sptrans.scd.auth.adapter.out.persistence.entity.ClassificationPersonEntity;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassificationPersonMapper {
    @Mapping(target = "idUsuarioCadastro", source = "idUsuarioCadastro", qualifiedByName = "longToUser")
    @Mapping(target = "idUsuarioManutencao", source = "idUsuarioManutencao", qualifiedByName = "longToUser")
    ClassificationPerson toDomain(ClassificationPersonEntity entity);

    @Mapping(target = "idUsuarioCadastro", source = "idUsuarioCadastro", qualifiedByName = "userToLong")
    @Mapping(target = "idUsuarioManutencao", source = "idUsuarioManutencao", qualifiedByName = "userToLong")
    ClassificationPersonEntity toEntity(ClassificationPerson domain);

    @Named("longToUser")
    public static User longToUser(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setIdUsuario(id);
        return user;
    }

    @Named("userToLong")
    public static Long userToLong(User user) {
        return user != null ? user.getIdUsuario() : null;
    }
}
