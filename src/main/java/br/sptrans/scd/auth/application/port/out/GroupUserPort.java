package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.GroupUserKey;

public interface GroupUserPort {

    /**
     * Lista todas as associações grupo-usuário.
     */
    List<GroupUser> listGroupUsers();

    /**
     * Lista todas as associações grupo-usuário paginadas.
     */
    Page<GroupUser> listGroupUsers(Pageable pageable);

    /**
     * Lista todas as associações grupo-usuário de um grupo específico (apenas ativos).
     */
    List<GroupUser> listGroupUsersByCodGrupo(String codGrupo);



    Optional<GroupUser> findById(GroupUserKey id);

    List<GroupUser> findById_IdUsuarioAndCodStatus(Long idUsuario, String codStatus);

    Page<GroupUser> listGroupsByUser(Long idUsuario, Pageable pageable);

    GroupUser save(GroupUser entity);

    void delete(GroupUser entity);

    void deleteById(GroupUserKey id);

    long count();
}
