package br.sptrans.scd.auth.adapter.out.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.GroupRepository;
import br.sptrans.scd.auth.domain.Group;
import br.sptrans.scd.auth.domain.GroupProfile;
import br.sptrans.scd.auth.domain.Profile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class GroupAdapterJpa implements GroupRepository {

    @PersistenceContext
    private EntityManager em;
    // ── buscarPorCodigo ───────────────────────────────────────────────────────

    @Override
    public Optional<Group> findById(String codGrupo) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT g.COD_GRUPO, g.NOM_GRUPO, g.COD_STATUS
                FROM   SPTRANSDBA.GRUPOS g
                WHERE  g.COD_GRUPO = :cod
                """)
                .setParameter("cod", codGrupo)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = (Object[]) rows.get(0);
        Group grupo = mapearGrupo(row);
        grupo.setPerfis(carregarPerfisDoGrupo(codGrupo));
        return Optional.of(grupo);
    }

    @Override
    public boolean existsByCode(String codGrupo) {
        Long count = ((Number) em.createNativeQuery("""
                    SELECT COUNT(*) FROM SPTRANSDBA.GRUPOS WHERE COD_GRUPO = :cod
                    """)
                .setParameter("cod", codGrupo)
                .getSingleResult()).longValue();
        return count > 0;
    }

    @Override
    public List<Group> listGroups(String codStatus) {
        String sql = "SELECT COD_GRUPO, NOM_GRUPO, COD_STATUS FROM SPTRANSDBA.GRUPOS";
        if (codStatus != null) {
            sql += " WHERE COD_STATUS = :status";
        }
        var query = em.createNativeQuery(sql);
        if (codStatus != null) {
            query.setParameter("status", codStatus);
        }
        List<Object[]> rows = query.getResultList();
        return rows.stream()
                .map(this::mapearGrupo)
                .toList();
    }

    @Override
    public void save(Group grupo) {
        em.createNativeQuery("""
                INSERT INTO SPTRANSDBA.GRUPOS (COD_GRUPO, NOM_GRUPO, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MODI)
                VALUES (:codGrupo, :nomGrupo, :codStatus, :idUsuarioManutencao, :dtModi)
            """)
                .setParameter("codGrupo", grupo.getCodGrupo())
                .setParameter("nomGrupo", grupo.getNomGrupo())
                .setParameter("codStatus", grupo.getCodStatus())
                .setParameter("idUsuarioManutencao", grupo.getIdUsuarioManutencao())
                .setParameter("dtModi", grupo.getDtModi())
                .executeUpdate();
    }

    @Override
    public void updateStatus(String codGrupo, String codStatus, Long idUsuarioManutencao) {
        em.createNativeQuery("""
                UPDATE SPTRANSDBA.GRUPOS SET COD_STATUS = :codStatus, ID_USUARIO_MANUTENCAO = :idUsuarioManutencao, DT_MODI = CURRENT_DATE
                WHERE COD_GRUPO = :codGrupo
            """)
                .setParameter("codStatus", codStatus)
                .setParameter("idUsuarioManutencao", idUsuarioManutencao)
                .setParameter("codGrupo", codGrupo)
                .executeUpdate();
    }

    @Override
    public void associateProfilesToGroup(String codGrupo, String codPerfil, Long idUsuarioManutencao) {
        em.createNativeQuery("""
                INSERT INTO SPTRANSDBA.GRUPO_PERFIS (COD_GRUPO, COD_PERFIL, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MODI)
                VALUES (:codGrupo, :codPerfil, 'A', :idUsuarioManutencao, CURRENT_DATE)
            """)
                .setParameter("codGrupo", codGrupo)
                .setParameter("codPerfil", codPerfil)
                .setParameter("idUsuarioManutencao", idUsuarioManutencao)
                .executeUpdate();
    }

    @Override
    public void disassociateProfileFromGroup(String codGrupo, String codPerfil, Long idUsuarioManutencao) {
        em.createNativeQuery("""
                UPDATE SPTRANSDBA.GRUPO_PERFIS SET COD_STATUS = 'I', ID_USUARIO_MANUTENCAO = :idUsuarioManutencao, DT_MODI = CURRENT_DATE
                WHERE COD_GRUPO = :codGrupo AND COD_PERFIL = :codPerfil
            """)
                .setParameter("idUsuarioManutencao", idUsuarioManutencao)
                .setParameter("codGrupo", codGrupo)
                .setParameter("codPerfil", codPerfil)
                .executeUpdate();
    }

    @Override
    public boolean isProfileAssociate(String codGrupo, String codPerfil) {
        Long count = ((Number) em.createNativeQuery("""
                SELECT COUNT(*) FROM SPTRANSDBA.GRUPO_PERFIS WHERE COD_GRUPO = :codGrupo AND COD_PERFIL = :codPerfil AND COD_STATUS = 'A'
            """)
                .setParameter("codGrupo", codGrupo)
                .setParameter("codPerfil", codPerfil)
                .getSingleResult()).longValue();
        return count > 0;
    }

    @Override
    public long countUserActive(String codGrupo) {
        Long count = ((Number) em.createNativeQuery("""
                SELECT COUNT(*) FROM SPTRANSDBA.GRUPO_USUARIOS WHERE COD_GRUPO = :codGrupo AND COD_STATUS = 'A'
            """)
                .setParameter("codGrupo", codGrupo)
                .getSingleResult()).longValue();
        return count;
    }

    // ── Helpers de mapeamento ─────────────────────────────────────────────────
    private Group mapearGrupo(Object[] row) {
        Group g = new Group();
        g.setCodGrupo((String) row[0]);
        g.setNomGrupo((String) row[1]);
        g.setCodStatus((String) row[2]);
        return g;
    }

    @SuppressWarnings("unchecked")
    private Set<GroupProfile> carregarPerfisDoGrupo(String codGrupo) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT p.COD_PERFIL, p.NOM_PERFIL, p.COD_STATUS
                FROM   SPTRANSDBA.GRUPO_PERFIS gp
                JOIN   SPTRANSDBA.PERFIS p ON p.COD_PERFIL = gp.COD_PERFIL
                WHERE  gp.COD_GRUPO  = :g
                AND    gp.COD_STATUS = 'A'
                ORDER BY p.NOM_PERFIL
                """)
                .setParameter("g", codGrupo)
                .getResultList();

        Set<GroupProfile> perfis = new java.util.HashSet<>();
        for (Object[] row : rows) {
            Profile p = new Profile();
            p.setCodPerfil((String) row[0]);
            p.setNomPerfil((String) row[1]);
            p.setCodStatus((String) row[2]);

            GroupProfile gp = new GroupProfile();
            gp.setPerfil(p);
            // Se necessário, preencha outros campos de GroupProfile aqui
            perfis.add(gp);
        }
        return perfis;
    }
}
