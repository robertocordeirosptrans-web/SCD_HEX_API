package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.GroupProfileKey;

public interface GroupProfileRepository {

    Optional<GroupProfile> findByCodGrupoAndCodPerfil(String codGrupo, String codPerfil);

    List<GroupProfile> findByCodGrupoCodStatus(String codGrupo, String codStatus);

    Optional<GroupProfile> findByCodGrupoPerfil(GroupProfileKey id);

    List<GroupProfile> findAllGroupProfile();

    GroupProfile saveGroupProfile(GroupProfile entity);

    void deleteGroupProfile(GroupProfile entity);

    void deleteByIdGroupProfile(GroupProfileKey id);

    long countGroupProfile();
}
