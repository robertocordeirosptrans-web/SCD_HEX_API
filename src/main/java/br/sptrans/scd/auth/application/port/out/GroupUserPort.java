package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.GroupUserKey;

public interface GroupUserPort {

    Optional<GroupUser> findById_IdUsuarioAndId_CodGrupo(Long idUsuario, String codGrupo);

    List<GroupUser> findById_IdUsuarioAndCodStatus(Long idUsuario, String codStatus);

    List<GroupUser> findById_CodGrupoAndCodStatus(String codGrupo, String codStatus);

    List<GroupUser> findById_IdUsuario(Long idUsuario);

    Optional<GroupUser> findById(GroupUserKey id);

    List<GroupUser> listGroupUsers();

    GroupUser save(GroupUser entity);

    void delete(GroupUser entity);

    void deleteById(GroupUserKey id);

    long count();
}
