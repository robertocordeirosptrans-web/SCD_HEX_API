package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.Profile;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.domain.enums.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class UserAdapterJpa implements UserRepository {

	private static final Logger log = LoggerFactory.getLogger(UserAdapterJpa.class);

	private static final String USUARIOS_SELECT =
		"SELECT ID_USUARIO, COD_LOGIN, COD_SENHA, NOM_USUARIO, DES_ENDERECO, "
		+ "NOM_DEPARTAMENTO, NOM_CARGO, NOM_FUNCAO, COD_CPF, COD_RG, NOM_EMAIL, "
		+ "COD_EMPRESA, COD_CLASSIFICACAO_PESSOA, COD_STATUS, NUM_TENTATIVA, "
		+ "NUM_DIAS_SEMANAS_PERMITIDOS, DT_JORNADA_INI, DT_JORNADA_FIM, DT_CRIACAO, "
		+ "DT_MODI, DT_EXPIRA_SENHA, DT_ULTIMO_ACESSO FROM SPTRANSDBA.USUARIOS";

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
		@SuppressWarnings("unchecked")
		List<Object[]> rows = em.createNativeQuery(
				USUARIOS_SELECT + " WHERE ID_USUARIO = :id")
				.setParameter("id", id)
				.getResultList();
		if (rows.isEmpty()) return Optional.empty();
		return Optional.of(mapToUser(rows.get(0)));
	}

	@Override
	public Optional<User> findByCodLogin(String codLogin) {
		Long total = ((Number) em.createNativeQuery(
				"SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS")
				.getSingleResult()).longValue();
		log.info("findByCodLogin: buscando '{}' | total de usuários na tabela: {}", codLogin, total);

		@SuppressWarnings("unchecked")
		List<Object[]> rows = em.createNativeQuery(
				USUARIOS_SELECT + " WHERE UPPER(TRIM(COD_LOGIN)) = UPPER(TRIM(:login))")
				.setParameter("login", codLogin)
				.getResultList();
		log.info("findByCodLogin: resultado da busca -> {} registro(s) encontrado(s)", rows.size());
		if (rows.isEmpty()) return Optional.empty();
		return Optional.of(mapToUser(rows.get(0)));
	}

	@Override
	public Optional<User> findByNomEmail(String nomEmail) {
		@SuppressWarnings("unchecked")
		List<Object[]> rows = em.createNativeQuery(
				USUARIOS_SELECT + " WHERE NOM_EMAIL = :email")
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
		@SuppressWarnings("unchecked")
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
			if (row[0] != null) func.setCodSistema(row[0].toString());
			if (row[1] != null) func.setCodModulo(row[1].toString());
			if (row[2] != null) func.setCodRotina(row[2].toString());
			if (row[3] != null) func.setCodFuncionalidade(row[3].toString());
			if (row[4] != null) func.setNomFuncionalidade(row[4].toString());
			funcionalidades.add(func);
		}
		return funcionalidades;
	}

	@Override
	public Set<Profile> carregarPerfisEfetivos(Long idUsuario) {
		// Perfis diretos (USUARIO_PERFIS) + perfis via grupos (GRUPO_USUARIOS → GRUPO_PERFIS)
		@SuppressWarnings("unchecked")
				List<Object[]> rows = em.createNativeQuery("""
						SELECT DISTINCT p.COD_PERFIL, p.NOM_PERFIL, p.COD_STATUS, up.DT_INICIO_VALIDADE, up.DT_FIM_VALIDADE
						FROM SPTRANSDBA.PERFIS p
						JOIN SPTRANSDBA.USUARIO_PERFIS up ON up.COD_PERFIL = p.COD_PERFIL
						WHERE p.COD_STATUS = 'A' AND up.ID_USUARIO = :id AND up.COD_STATUS = 'A'
				""")
								.setParameter("id", idUsuario)
								.getResultList();
				Set<Profile> perfis = new HashSet<>();
				for (Object[] row : rows) {
						Profile perfil = new Profile();
						if (row[0] != null) perfil.setCodPerfil(row[0].toString());
						if (row[1] != null) perfil.setNomPerfil(row[1].toString());
						if (row[2] != null) perfil.setCodStatus(row[2].toString());
						// Se necessário, adicione setters para dtInicioValidade e dtFimValidade
						perfis.add(perfil);
				}
				return perfis;
	}

	@Override
	public boolean existsByLogin(String codLogin) {
		Long count = ((Number) em.createNativeQuery("""
				SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS WHERE UPPER(COD_LOGIN) = UPPER(:login)
				""")
				.setParameter("login", codLogin)
				.getSingleResult()).longValue();
		return count > 0;
	}

	@Override
	public List<User> findAll(String codStatus) {
		String sql = USUARIOS_SELECT;
		if (codStatus != null) {
			sql += " WHERE COD_STATUS = :status";
		}
		var query = em.createNativeQuery(sql);
		if (codStatus != null) {
			query.setParameter("status", codStatus);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();
		List<User> users = new ArrayList<>();
		for (Object row : rows) {
			users.add(mapToUser((Object[]) row));
		}
		return users;
	}

	private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
		"ID_USUARIO", "COD_LOGIN", "NOM_USUARIO", "NOM_EMAIL",
		"COD_STATUS", "DT_CRIACAO", "DT_MODI", "DT_ULTIMO_ACESSO"
	);

	@Override
	public List<User> findAllPaginated(String codStatus, String search, int offset, int limit, String sortBy, String sortDir) {
		StringBuilder sql = new StringBuilder(USUARIOS_SELECT);
		List<String> conditions = new ArrayList<>();

		if (codStatus != null && !codStatus.isBlank()) {
			conditions.add("COD_STATUS = :status");
		}
		if (search != null && !search.isBlank()) {
			conditions.add("(UPPER(NOM_USUARIO) LIKE :search OR UPPER(COD_LOGIN) LIKE :search OR UPPER(NOM_EMAIL) LIKE :search OR UPPER(COD_CPF) LIKE :search)");
		}

		if (!conditions.isEmpty()) {
			sql.append(" WHERE ").append(String.join(" AND ", conditions));
		}

		// Ordenação segura — só colunas permitidas
		String safeSort = ALLOWED_SORT_COLUMNS.contains(sortBy.toUpperCase()) ? sortBy.toUpperCase() : "ID_USUARIO";
		String safeDir = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
		sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeDir);

		sql.append(" OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY");

		var query = em.createNativeQuery(sql.toString());
		if (codStatus != null && !codStatus.isBlank()) {
			query.setParameter("status", codStatus);
		}
		if (search != null && !search.isBlank()) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}
		query.setParameter("offset", offset);
		query.setParameter("limit", limit);

		@SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();
		List<User> users = new ArrayList<>();
		for (Object row : rows) {
			users.add(mapToUser((Object[]) row));
		}
		return users;
	}

	@Override
	public long countAll(String codStatus, String search) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM SPTRANSDBA.USUARIOS");
		List<String> conditions = new ArrayList<>();

		if (codStatus != null && !codStatus.isBlank()) {
			conditions.add("COD_STATUS = :status");
		}
		if (search != null && !search.isBlank()) {
			conditions.add("(UPPER(NOM_USUARIO) LIKE :search OR UPPER(COD_LOGIN) LIKE :search OR UPPER(NOM_EMAIL) LIKE :search OR UPPER(COD_CPF) LIKE :search)");
		}

		if (!conditions.isEmpty()) {
			sql.append(" WHERE ").append(String.join(" AND ", conditions));
		}

		var query = em.createNativeQuery(sql.toString());
		if (codStatus != null && !codStatus.isBlank()) {
			query.setParameter("status", codStatus);
		}
		if (search != null && !search.isBlank()) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}

		return ((Number) query.getSingleResult()).longValue();
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
	// Índices correspondem a USUARIOS_SELECT:
	// 0:ID_USUARIO, 1:COD_LOGIN, 2:COD_SENHA, 3:NOM_USUARIO, 4:DES_ENDERECO,
	// 5:NOM_DEPARTAMENTO, 6:NOM_CARGO, 7:NOM_FUNCAO, 8:COD_CPF, 9:COD_RG,
	// 10:NOM_EMAIL, 11:COD_EMPRESA, 12:COD_CLASSIFICACAO_PESSOA, 13:COD_STATUS,
	// 14:NUM_TENTATIVA, 15:NUM_DIAS_SEMANAS_PERMITIDOS, 16:DT_JORNADA_INI,
	// 17:DT_JORNADA_FIM, 18:DT_CRIACAO, 19:DT_MODI, 20:DT_EXPIRA_SENHA, 21:DT_ULTIMO_ACESSO
	private User mapToUser(Object[] row) {
		User user = new User();
		user.setIdUsuario(row[0] != null ? ((Number) row[0]).longValue() : null);
		user.setCodLogin(row[1] != null ? row[1].toString() : null);
		user.setCodSenha(row[2] != null ? row[2].toString() : null);
		user.setNomUsuario(row[3] != null ? row[3].toString() : null);
		user.setDesEndereco(row[4] != null ? row[4].toString() : null);
		user.setNomDepartamento(row[5] != null ? row[5].toString() : null);
		user.setNomCargo(row[6] != null ? row[6].toString() : null);
		user.setNomFuncao(row[7] != null ? row[7].toString() : null);
		user.setCodCpf(row[8] != null ? row[8].toString() : null);
		user.setCodRg(row[9] != null ? row[9].toString() : null);
		user.setNomEmail(row[10] != null ? row[10].toString() : null);
		user.setCodEmpresa(row[11] != null ? row[11].toString() : null);
		if (row[12] != null) {
			String codClassificacao = row[12].toString();
			ClassificationPerson cp = null;
			try {
				Object[] result = (Object[]) em.createNativeQuery(
					"SELECT COD_CLASSIFICACAO_PESSOA, DES_CLASSIFICACAO_PESSOA FROM SPTRANSDBA.CLASSIFICACOES_PESSOAS WHERE COD_CLASSIFICACAO_PESSOA = :cod")
					.setParameter("cod", codClassificacao)
					.getSingleResult();
				cp = new ClassificationPerson();
				cp.setCodClassificacaoPessoa(result[0] != null ? result[0].toString() : null);
				cp.setDesClassificacaoPessoa(result[1] != null ? result[1].toString() : null);
			} catch (Exception e) {
				// fallback: cria só com o código
				cp = new ClassificationPerson();
				cp.setCodClassificacaoPessoa(codClassificacao);
			}
			user.setCodClassificacaoPessoa(cp);
		}
		if (row[13] != null) {
			String code = row[13].toString();
			for (UserStatus s : UserStatus.values()) {
				if (s.getCode().equals(code)) { user.setStatus(s); break; }
			}
		}
		user.setNumTentativasFalha(row[14] != null ? ((Number) row[14]).intValue() : 0);
		user.setNumDiasSemanasPermitidos(row[15] != null ? row[15].toString() : null);
		user.setDt_jornada_ini(row[16] != null ? (Date) row[16] : null);
		user.setDt_jornada_fim(row[17] != null ? (Date) row[17] : null);
		user.setDtCriacao(toLocalDateTime(row[18]));
		user.setDtModi(toLocalDateTime(row[19]));
		user.setDtExpiraSenha(toLocalDateTime(row[20]));
		user.setDtUltimoAcesso(toLocalDateTime(row[21]));
		return user;
	}

	private LocalDateTime toLocalDateTime(Object obj) {
		if (obj instanceof Timestamp ts) return ts.toLocalDateTime();
		if (obj instanceof java.sql.Date d) return d.toLocalDate().atStartOfDay();
		return null;
	}
}
