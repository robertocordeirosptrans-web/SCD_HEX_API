package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.ProfileRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;
import br.sptrans.scd.auth.domain.Profile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class ProfileAdapterJpa implements ProfileRepository {
	@PersistenceContext
	private EntityManager em;

	@Override
	public Optional<Profile> findById(String codPerfil) {
		List<Object[]> rows = em.createNativeQuery("""
				SELECT COD_PERFIL, NOM_PERFIL, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MANUTENCAO
				FROM SPTRANSDBA.PERFIS
				WHERE COD_PERFIL = :codPerfil
				""")
				.setParameter("codPerfil", codPerfil)
				.getResultList();
		if (rows.isEmpty()) {
			return Optional.empty();
		}
		Object[] row = rows.get(0);
		Profile perfil = new Profile();
		perfil.setCodPerfil((String) row[0]);
		perfil.setNomPerfil((String) row[1]);
		perfil.setCodStatus((String) row[2]);
		perfil.setIdUsuarioManutencao(row[3] != null ? ((Number) row[3]).longValue() : null);
		perfil.setDtModi(row[4] != null ? ((java.sql.Timestamp) row[4]).toLocalDateTime() : null);
		return Optional.of(perfil);
	}

	@Override
	public boolean existsByCode(String codPerfil) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.PERFIS WHERE COD_PERFIL = :codPerfil
				""")
				.setParameter("codPerfil", codPerfil)
				.getSingleResult()).longValue();
		return count > 0;
	}

	@Override
	public List<Profile> listProfile(String codStatus) {
		String sql = "SELECT COD_PERFIL, NOM_PERFIL, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MANUTENCAO FROM SPTRANSDBA.PERFIS";
		if (codStatus != null) {
			sql += " WHERE COD_STATUS = :status";
		}
		var query = em.createNativeQuery(sql);
		if (codStatus != null) {
			query.setParameter("status", codStatus);
		}
		List<Object[]> rows = query.getResultList();
		return rows.stream().map(row -> {
			Profile perfil = new Profile();
			perfil.setCodPerfil((String) row[0]);
			perfil.setNomPerfil((String) row[1]);
			perfil.setCodStatus((String) row[2]);
			perfil.setIdUsuarioManutencao(row[3] != null ? ((Number) row[3]).longValue() : null);
			perfil.setDtModi(row[4] != null ? ((java.sql.Timestamp) row[4]).toLocalDateTime() : null);
			return perfil;
		}).toList();
	}

	@Override
	public void save(Profile perfil) {
		em.createNativeQuery("""
				INSERT INTO SPTRANSDBA.PERFIS (COD_PERFIL, NOM_PERFIL, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MANUTENCAO)
				VALUES (:codPerfil, :nomPerfil, :codStatus, :idUsuarioManutencao, :dtManutencao)
			""")
			.setParameter("codPerfil", perfil.getCodPerfil())
			.setParameter("nomPerfil", perfil.getNomPerfil())
			.setParameter("codStatus", perfil.getCodStatus())
			.setParameter("idUsuarioManutencao", perfil.getIdUsuarioManutencao())
			.setParameter("dtManutencao", perfil.getDtModi())
			.executeUpdate();
	}

	@Override
	public void updateStatus(String codPerfil, String codStatus, Long idUsuarioManutencao) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.PERFIS SET COD_STATUS = :codStatus, ID_USUARIO_MANUTENCAO = :idUsuarioManutencao, DT_MANUTENCAO = CURRENT_DATE
				WHERE COD_PERFIL = :codPerfil
			""")
			.setParameter("codStatus", codStatus)
			.setParameter("idUsuarioManutencao", idUsuarioManutencao)
			.setParameter("codPerfil", codPerfil)
			.executeUpdate();
	}

	@Override
	public void associateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave, Long idUsuarioManutencao) {
		em.createNativeQuery("""
				INSERT INTO SPTRANSDBA.PERFIL_FUNCIONALIDADES (COD_PERFIL, COD_SISTEMA, COD_MODULO, COD_ROTINA, COD_FUNCIONALIDADE, COD_STATUS, ID_USUARIO_MANUTENCAO, DT_MANUTENCAO)
				VALUES (:codPerfil, :codSistema, :codModulo, :codRotina, :codFuncionalidade, 'A', :idUsuarioManutencao, CURRENT_DATE)
			""")
			.setParameter("codPerfil", codPerfil)
			.setParameter("codSistema", chave.getCodSistema())
			.setParameter("codModulo", chave.getCodModulo())
			.setParameter("codRotina", chave.getCodRotina())
			.setParameter("codFuncionalidade", chave.getCodFuncionalidade())
			.setParameter("idUsuarioManutencao", idUsuarioManutencao)
			.executeUpdate();
	}

	@Override
	public void desassociateFunctionalitiesToProfile(String codPerfil, FunctionalityKey chave, Long idUsuarioManutencao) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.PERFIL_FUNCIONALIDADES SET COD_STATUS = 'I', ID_USUARIO_MANUTENCAO = :idUsuarioManutencao, DT_MANUTENCAO = CURRENT_DATE
				WHERE COD_PERFIL = :codPerfil AND COD_SISTEMA = :codSistema AND COD_MODULO = :codModulo AND COD_ROTINA = :codRotina AND COD_FUNCIONALIDADE = :codFuncionalidade
			""")
			.setParameter("idUsuarioManutencao", idUsuarioManutencao)
			.setParameter("codPerfil", codPerfil)
			.setParameter("codSistema", chave.getCodSistema())
			.setParameter("codModulo", chave.getCodModulo())
			.setParameter("codRotina", chave.getCodRotina())
			.setParameter("codFuncionalidade", chave.getCodFuncionalidade())
			.executeUpdate();
	}

	@Override
	public boolean isFunctionalityAssociate(String codPerfil, FunctionalityKey chave) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.PERFIL_FUNCIONALIDADES
				WHERE COD_PERFIL = :codPerfil AND COD_SISTEMA = :codSistema AND COD_MODULO = :codModulo AND COD_ROTINA = :codRotina AND COD_FUNCIONALIDADE = :codFuncionalidade AND COD_STATUS = 'A'
			""")
			.setParameter("codPerfil", codPerfil)
			.setParameter("codSistema", chave.getCodSistema())
			.setParameter("codModulo", chave.getCodModulo())
			.setParameter("codRotina", chave.getCodRotina())
			.setParameter("codFuncionalidade", chave.getCodFuncionalidade())
			.getSingleResult()).longValue();
		return count > 0;
	}

	@Override
	public List<Functionality> listFunctionalityActive() {
		List<Object[]> rows = em.createNativeQuery("""
				SELECT COD_SISTEMA, COD_MODULO, COD_ROTINA, COD_FUNCIONALIDADE, NOM_FUNCIONALIDADE
				FROM SPTRANSDBA.FUNCIONALIDADES
				WHERE COD_STATUS = 'A'
			""")
			.getResultList();
		return rows.stream().map(row -> {
			Functionality func = new Functionality();
			func.setCodSistema((String) row[0]);
			func.setCodModulo((String) row[1]);
			func.setCodRotina((String) row[2]);
			func.setCodFuncionalidade((String) row[3]);
			func.setNomFuncionalidade((String) row[4]);
			return func;
		}).toList();
	}

	@Override
	public boolean isFunctionality(FunctionalityKey chave) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.FUNCIONALIDADES
				WHERE COD_SISTEMA = :codSistema AND COD_MODULO = :codModulo AND COD_ROTINA = :codRotina AND COD_FUNCIONALIDADE = :codFuncionalidade AND COD_STATUS = 'A'
			""")
			.setParameter("codSistema", chave.getCodSistema())
			.setParameter("codModulo", chave.getCodModulo())
			.setParameter("codRotina", chave.getCodRotina())
			.setParameter("codFuncionalidade", chave.getCodFuncionalidade())
			.getSingleResult()).longValue();
		return count > 0;
	}

	@Override
	public long countUserActive(String codPerfil) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.USUARIO_PERFIS
				WHERE COD_PERFIL = :codPerfil AND COD_STATUS = 'A'
			""")
			.setParameter("codPerfil", codPerfil)
			.getSingleResult()).longValue();
		return count;
	}
}
