package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;

public interface GroupProfileRepository {

    Optional<GroupProfile> findById_CodGrupoAndId_CodPerfil(String codGrupo, String codPerfil);

    List<GroupProfile> findById_CodGrupoAndCodStatus(String codGrupo, String codStatus);

    Optional<GroupProfile> findById(GroupProfileKey id);

    List<GroupProfile> findAll();

    GroupProfile save(GroupProfile entity);

    void delete(GroupProfile entity);

    void deleteById(GroupProfileKey id);

    long count();
}
