package br.sptrans.scd.auth.adapter.out.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class UserAdapterJpa implements UserRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public User save(User user) {
		// Exemplo: insert ou update conforme existência
		if (user.getIdUsuario() == null) {
			insert(user);
		} else {
			update(user);
		}
		return user;
	}

	@Override
	public Optional<User> findById(Long id) {
		List<Object[]> rows = em.createNativeQuery("""
				SELECT * FROM SPTRANSDBA.USUARIOS WHERE ID_USUARIO = :id
				""")
				.setParameter("id", id)
				.getResultList();
		if (rows.isEmpty()) return Optional.empty();
		return Optional.of(mapToUser(rows.get(0)));
	}

	@Override
	public Optional<User> findByCodLogin(String codLogin) {
		List<Object[]> rows = em.createNativeQuery("""
				SELECT * FROM SPTRANSDBA.USUARIOS WHERE COD_LOGIN = :login
				""")
				.setParameter("login", codLogin)
				.getResultList();
		if (rows.isEmpty()) return Optional.empty();
		return Optional.of(mapToUser(rows.get(0)));
	}

	@Override
	public Optional<User> findByNomEmail(String nomEmail) {
		List<Object[]> rows = em.createNativeQuery("""
				SELECT * FROM SPTRANSDBA.USUARIOS WHERE NOM_EMAIL = :email
				""")
				.setParameter("email", nomEmail)
				.getResultList();
		if (rows.isEmpty()) return Optional.empty();
		return Optional.of(mapToUser(rows.get(0)));
	}

	@Override
	public void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET NUM_TENTATIVA = :numTentativas, COD_STATUS = :codStatus WHERE ID_USUARIO = :id
				""")
				.setParameter("numTentativas", numTentativas)
				.setParameter("codStatus", codStatus)
				.setParameter("id", idUsuario)
				.executeUpdate();
	}

	@Override
	public void atualizarUltimoAcesso(Long idUsuario) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET DT_ULTIMO_ACESSO = CURRENT_TIMESTAMP WHERE ID_USUARIO = :id
				""")
				.setParameter("id", idUsuario)
				.executeUpdate();
	}

	@Override
	public void insert(User usuario) {
		em.createNativeQuery("""
				INSERT INTO SPTRANSDBA.USUARIOS (COD_LOGIN, COD_SENHA, NOM_USUARIO, DES_ENDERECO, NOM_DEPARTAMENTO, NOM_CARGO, NOM_FUNCAO, COD_CPF, COD_RG, NOM_EMAIL, COD_CLASSIFICACAO_PESSOA, COD_EMPRESA, NUM_TENTATIVA, NUM_DIAS_SEMANAS_PERMITIDOS, DT_JORNADA_INI, DT_JORNADA_FIM, COD_STATUS, DT_CRIACAO)
				VALUES (:login, :senha, :nome, :endereco, :departamento, :cargo, :funcao, :cpf, :rg, :email, :classificacao, :empresa, :tentativas, :dias, :jornadaIni, :jornadaFim, :status, CURRENT_TIMESTAMP)
				""")
				.setParameter("login", usuario.getCodLogin())
				.setParameter("senha", usuario.getCodSenha())
				.setParameter("nome", usuario.getNomUsuario())
				.setParameter("endereco", usuario.getDesEndereco())
				.setParameter("departamento", usuario.getNomDepartamento())
				.setParameter("cargo", usuario.getNomCargo())
				.setParameter("funcao", usuario.getNomFuncao())
				.setParameter("cpf", usuario.getCodCpf())
				.setParameter("rg", usuario.getCodRg())
				.setParameter("email", usuario.getNomEmail())
				.setParameter("classificacao", usuario.getCodClassificacaoPessoa() != null ? usuario.getCodClassificacaoPessoa().getDesClassificacaoPessoa() : null)
				.setParameter("empresa", usuario.getCodEmpresa())
				.setParameter("tentativas", usuario.getNumTentativasFalha())
				.setParameter("dias", usuario.getNumDiasSemanasPermitidos())
				.setParameter("jornadaIni", usuario.getDt_jornada_ini())
				.setParameter("jornadaFim", usuario.getDt_jornada_fim())
				.setParameter("status", usuario.getStatus() != null ? usuario.getStatus().getCode() : null)
				.executeUpdate();
	}

	@Override
	public void update(User usuario) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET NOM_USUARIO = :nome, DES_ENDERECO = :endereco, NOM_DEPARTAMENTO = :departamento, NOM_CARGO = :cargo, NOM_FUNCAO = :funcao, COD_CPF = :cpf, COD_RG = :rg, NOM_EMAIL = :email, COD_CLASSIFICACAO_PESSOA = :classificacao, COD_EMPRESA = :empresa, NUM_TENTATIVA = :tentativas, NUM_DIAS_SEMANAS_PERMITIDOS = :dias, DT_JORNADA_INI = :jornadaIni, DT_JORNADA_FIM = :jornadaFim, COD_STATUS = :status, DT_MODI = CURRENT_TIMESTAMP WHERE ID_USUARIO = :id
				""")
				.setParameter("nome", usuario.getNomUsuario())
				.setParameter("endereco", usuario.getDesEndereco())
				.setParameter("departamento", usuario.getNomDepartamento())
				.setParameter("cargo", usuario.getNomCargo())
				.setParameter("funcao", usuario.getNomFuncao())
				.setParameter("cpf", usuario.getCodCpf())
				.setParameter("rg", usuario.getCodRg())
				.setParameter("email", usuario.getNomEmail())
				.setParameter("classificacao", usuario.getCodClassificacaoPessoa() != null ? usuario.getCodClassificacaoPessoa().getDesClassificacaoPessoa() : null)
				.setParameter("empresa", usuario.getCodEmpresa())
				.setParameter("tentativas", usuario.getNumTentativasFalha())
				.setParameter("dias", usuario.getNumDiasSemanasPermitidos())
				.setParameter("jornadaIni", usuario.getDt_jornada_ini())
				.setParameter("jornadaFim", usuario.getDt_jornada_fim())
				.setParameter("status", usuario.getStatus() != null ? usuario.getStatus().getCode() : null)
				.setParameter("id", usuario.getIdUsuario())
				.executeUpdate();
	}

	@Override
	public void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET COD_STATUS = :status, DT_MODI = CURRENT_TIMESTAMP WHERE ID_USUARIO = :id
				""")
				.setParameter("status", codStatus)
				.setParameter("id", idUsuario)
				.executeUpdate();
	}

	@Override
	public void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash, LocalDateTime expiryDate) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET COD_SENHA = :newSenha, OLD_SENHA = :oldSenha, DT_EXPIRA_SENHA = :expira WHERE ID_USUARIO = :id
				""")
				.setParameter("newSenha", newPasswordHash)
				.setParameter("oldSenha", oldPasswordHash)
				.setParameter("expira", expiryDate)
				.setParameter("id", idUsuario)
				.executeUpdate();
	}

	@Override
	public void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET NUM_TENTATIVA = 0, COD_STATUS = :status, DT_MODI = CURRENT_TIMESTAMP WHERE ID_USUARIO = :id
				""")
				.setParameter("status", codStatus)
				.setParameter("id", idUsuario)
				.executeUpdate();
	}

	@Override
	public void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni, Date jornadaFim, Long idUsuarioManutencao) {
		em.createNativeQuery("""
				UPDATE SPTRANSDBA.USUARIOS SET NUM_DIAS_SEMANAS_PERMITIDOS = :dias, DT_JORNADA_INI = :jornadaIni, DT_JORNADA_FIM = :jornadaFim, DT_MODI = CURRENT_TIMESTAMP WHERE ID_USUARIO = :id
				""")
				.setParameter("dias", diasPermitidos)
				.setParameter("jornadaIni", jornadaIni)
				.setParameter("jornadaFim", jornadaFim)
				.setParameter("id", idUsuario)
				.executeUpdate();
	}

	@Override
	public Set<Functionality> carregarFuncionalidadesEfetivas(Long idUsuario) {
		// Exemplo simplificado: apenas busca funcionalidades diretas
		List<Object[]> rows = em.createNativeQuery("""
			SELECT f.COD_SISTEMA, f.COD_MODULO, f.COD_ROTINA, f.COD_FUNCIONALIDADE, f.NOM_FUNCIONALIDADE
			FROM SPTRANSDBA.USUARIO_FUNCIONALIDADES uf
			JOIN SPTRANSDBA.FUNCIONALIDADES f ON f.COD_FUNCIONALIDADE = uf.COD_FUNCIONALIDADE
			WHERE uf.ID_USUARIO = :id AND uf.COD_STATUS_USU_FUN = 'A'
		""")
				.setParameter("id", idUsuario)
				.getResultList();
		Set<Functionality> funcionalidades = new HashSet<>();
		for (Object[] row : rows) {
			Functionality func = new Functionality();
			// Preencher campos conforme entidade Functionality
			funcionalidades.add(func);
		}
		return funcionalidades;
	}

	@Override
	public boolean existsByLogin(String codLogin) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS WHERE COD_LOGIN = :login
				""")
				.setParameter("login", codLogin)
				.getSingleResult()).longValue();
		return count > 0;
	}

	@Override
	public List<User> findAll(String codStatus) {
		String sql = "SELECT * FROM SPTRANSDBA.USUARIOS";
		if (codStatus != null) {
			sql += " WHERE COD_STATUS = :status";
		}
		var query = em.createNativeQuery(sql);
		if (codStatus != null) {
			query.setParameter("status", codStatus);
		}
		List<Object[]> rows = query.getResultList();
		List<User> users = new ArrayList<>();
		for (Object row : rows) {
			users.add(mapToUser((Object[]) row));
		}
		return users;
	}

	@Override
	public boolean hasActiveSession(Long idUsuario) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS WHERE ID_USUARIO = :id AND DT_ULTIMO_ACESSO > (CURRENT_TIMESTAMP - INTERVAL '30' MINUTE)
				""")
				.setParameter("id", idUsuario)
				.getSingleResult()).longValue();
		return count > 0;
	}

	// Helper para mapear Object[] para User
	private User mapToUser(Object[] row) {
		User user = new User();
		// Preencher campos conforme ordem do select ou usar ResultSetMapping
		// Exemplo:
		// user.setIdUsuario(((Number) row[0]).longValue());
		// ...
		// Supondo que COD_STATUS está em row[16] (ajuste conforme seu select)
		if (row.length > 16 && row[16] != null) {
			String codStatus = row[16].toString();
			try {
				user.setStatus(br.sptrans.scd.auth.domain.enums.UserStatus.valueOf(
					codStatus.equals("A") ? "ACTIVE" : codStatus.equals("B") ? "BLOCKED" : "INACTIVE"
				));
			} catch (Exception e) {
				user.setStatus(null);
			}
		}
		return user;
	}
}
