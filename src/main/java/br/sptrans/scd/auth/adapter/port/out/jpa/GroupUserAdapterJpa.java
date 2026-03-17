package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.GroupUserRepository;
import br.sptrans.scd.auth.domain.GroupUser;
import br.sptrans.scd.auth.domain.GroupUserKey;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class GroupUserAdapterJpa implements GroupUserRepository {
    @PersistenceContext
    private EntityManager em;


        @Override
        public Optional<GroupUser> findById_IdUsuarioAndId_CodGrupo(Long idUsuario, String codGrupo) {
            List<Object[]> rows = em.createNativeQuery("""
                SELECT gu.ID_USUARIO, gu.COD_GRUPO, gu.COD_STATUS, gu.ID_USUARIO_MANUTENCAO, gu.DT_MODI
                FROM SPTRANSDBA.GRUPO_USUARIOS gu
                WHERE gu.ID_USUARIO = :idUsuario AND gu.COD_GRUPO = :codGrupo
            """)
                .setParameter("idUsuario", idUsuario)
                .setParameter("codGrupo", codGrupo)
                .getResultList();
            if (rows.isEmpty()) return Optional.empty();
            Object[] row = rows.get(0);
            GroupUser gu = mapRowToGroupUser(row);
            return Optional.of(gu);
        }


    @Override
    public List<GroupUser> findById_IdUsuarioAndCodStatus(Long idUsuario, String codStatus) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT gu.ID_USUARIO, gu.COD_GRUPO, gu.COD_STATUS, gu.ID_USUARIO_MANUTENCAO, gu.DT_MODI
            FROM SPTRANSDBA.GRUPO_USUARIOS gu
            WHERE gu.ID_USUARIO = :idUsuario AND gu.COD_STATUS = :codStatus
        """)
            .setParameter("idUsuario", idUsuario)
            .setParameter("codStatus", codStatus)
            .getResultList();
        return rows.stream().map(this::mapRowToGroupUser).toList();
    }


    @Override
    public List<GroupUser> findById_CodGrupoAndCodStatus(String codGrupo, String codStatus) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT gu.ID_USUARIO, gu.COD_GRUPO, gu.COD_STATUS, gu.ID_USUARIO_MANUTENCAO, gu.DT_MODI
            FROM SPTRANSDBA.GRUPO_USUARIOS gu
            WHERE gu.COD_GRUPO = :codGrupo AND gu.COD_STATUS = :codStatus
        """)
            .setParameter("codGrupo", codGrupo)
            .setParameter("codStatus", codStatus)
            .getResultList();
        return rows.stream().map(this::mapRowToGroupUser).toList();
    }


    @Override
    public List<GroupUser> findById_IdUsuario(Long idUsuario) {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT gu.ID_USUARIO, gu.COD_GRUPO, gu.COD_STATUS, gu.ID_USUARIO_MANUTENCAO, gu.DT_MODI
            FROM SPTRANSDBA.GRUPO_USUARIOS gu
            WHERE gu.ID_USUARIO = :idUsuario
        """)
            .setParameter("idUsuario", idUsuario)
            .getResultList();
        return rows.stream().map(this::mapRowToGroupUser).toList();
    }


    @Override
    public Optional<GroupUser> findById(GroupUserKey id) {
        return findById_IdUsuarioAndId_CodGrupo(id.getIdUsuario(), id.getCodGrupo());
    }


    @Override
    public List<GroupUser> findAll() {
        List<Object[]> rows = em.createNativeQuery("""
            SELECT gu.ID_USUARIO, gu.COD_GRUPO, gu.COD_STATUS, gu.ID_USUARIO_MANUTENCAO, gu.DT_MODI
            FROM SPTRANSDBA.GRUPO_USUARIOS gu
        """)
            .getResultList();
        return rows.stream().map(this::mapRowToGroupUser).toList();
    }


    @Override
    public GroupUser save(GroupUser entity) {
        // Exemplo: insert ou update conforme existência
        if (findById(entity.getId()).isPresent()) {
            em.createNativeQuery("""
                UPDATE SPTRANSDBA.GRUPO_USUARIOS SET COD_STATUS = :codStatus, ID_USUARIO_MANUTENCAO = :idUsuarioManutencao, DT_MODI = :dtModi
                WHERE ID_USUARIO = :idUsuario AND COD_GRUPO = :codGrupo
            """)
                .setParameter("codStatus", entity.getCodStatus())
                .setParameter("idUsuarioManutencao", entity.getIdUsuarioManutencao())
                .setParameter("dtModi", entity.getDtModi())
                .setParameter("idUsuario", entity.getId().getIdUsuario())
                .setParameter("codGrupo", entity.getId().getCodGrupo())
                .executeUpdate();
        } else {
            em.createNativeQuery("""
                INSERT INTO SPTRANSDBA.GRUPO_USUARIOS (ID_USUARIO, COD_GRUPO, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MODI)
                VALUES (:idUsuario, :codGrupo, :codStatus, :idUsuarioManutencao, :dtModi)
            """)
                .setParameter("idUsuario", entity.getId().getIdUsuario())
                .setParameter("codGrupo", entity.getId().getCodGrupo())
                .setParameter("codStatus", entity.getCodStatus())
                .setParameter("idUsuarioManutencao", entity.getIdUsuarioManutencao())
                .setParameter("dtModi", entity.getDtModi())
                .executeUpdate();
        }
        return entity;
    }


    @Override
    public void delete(GroupUser entity) {
        deleteById(entity.getId());
    }


    @Override
    public void deleteById(GroupUserKey id) {
        em.createNativeQuery("""
            DELETE FROM SPTRANSDBA.GRUPO_USUARIOS WHERE ID_USUARIO = :idUsuario AND COD_GRUPO = :codGrupo
        """)
            .setParameter("idUsuario", id.getIdUsuario())
            .setParameter("codGrupo", id.getCodGrupo())
            .executeUpdate();
    }


    @Override
    public long count() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM SPTRANSDBA.GRUPO_USUARIOS").getSingleResult()).longValue();
    }

    private GroupUser mapRowToGroupUser(Object[] row) {
        GroupUser gu = new GroupUser();
        gu.setId(new GroupUserKey(
            row[0] != null ? ((Number) row[0]).longValue() : null,
            row[1] != null ? row[1].toString() : null));
        gu.setCodStatus(row[2] != null ? row[2].toString() : null);
        gu.setIdUsuarioManutencao(row[3] != null ? ((Number) row[3]).longValue() : null);
        gu.setDtModi(row[4] != null ? ((java.sql.Timestamp) row[4]).toLocalDateTime() : null);
        return gu;
    }
}
