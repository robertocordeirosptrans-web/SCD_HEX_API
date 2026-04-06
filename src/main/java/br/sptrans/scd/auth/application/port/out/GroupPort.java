package br.sptrans.scd.auth.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupUser;

/**
 * Porta de Saída — repositório de Grupos. Tabela principal: SPTRANSDBA.GRUPOS.
 * Tabelas de associação: SPTRANSDBA.GRUPO_PERFIS, SPTRANSDBA.GRUPO_USUARIOS.
 */
public interface GroupPort extends GroupProfilePort, GroupUserPort {

    Optional<Group> findById(String codGrupo);

    /**
     * Verifica se o código já está em uso (para garantir unicidade na criação).
     */
    boolean existsByCode(String codGrupo);

    List<Group> listGroups(String codStatus);
    Page<Group> listGroups(String codStatus, Pageable pageable);

    void save(Group grupo);

    void updateStatus(String codGrupo, String codStatus, Long idUsuarioManutencao);

    // ── Associações GRUPO_PERFIS ──────────────────────────────────────────────
    /**
     * Insere linha em GRUPO_PERFIS com COD_STATUS = 'A'.
     */
    void associateProfilesToGroup(String codGrupo, String codPerfil, Long idUsuarioManutencao);

    /**
     * Atualiza COD_STATUS = 'I' em GRUPO_PERFIS para o par (codGrupo,
     * codPerfil). Não deleta o registro — mantém histórico.
     */
    void disassociateProfileFromGroup(String codGrupo, String codPerfil, Long idUsuarioManutencao);

    /**
     * Verifica se o perfil já está associado ao grupo com status Ativo.
     */
    boolean isProfileAssociate(String codGrupo, String codPerfil);

    // ── Verificação de dependências ───────────────────────────────────────────
    /**
     * Conta quantos usuários ativos (COD_STATUS = 'A') estão vinculados ao
     * grupo via GRUPO_USUARIOS. Usado antes de inativar.
     */
    long countUserActive(String codGrupo);

    /**
     * Lista todas as associações grupo-usuário.
     */
    List<GroupUser> listGroupUsers();
    Page<GroupUser> listGroupUsers(Pageable pageable);
}
