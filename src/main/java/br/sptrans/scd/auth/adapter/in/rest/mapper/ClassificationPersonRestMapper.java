package br.sptrans.scd.auth.adapter.in.rest.mapper;


import br.sptrans.scd.auth.adapter.in.rest.dto.ClassificationPersonResponseDTO;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassificationPersonRestMapper {
    @Mapping(target = "idUsuarioCadastro", source = "idUsuarioCadastro", qualifiedByName = "userToLong")
    @Mapping(target = "idUsuarioManutencao", source = "idUsuarioManutencao", qualifiedByName = "userToLong")
    ClassificationPersonResponseDTO toDto(ClassificationPerson domain);

    @Named("userToLong")
    public static Long userToLong(User user) {
        return user != null ? user.getIdUsuario() : null;
    }
}
