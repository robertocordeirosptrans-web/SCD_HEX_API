package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.auth.domain.vo.AccessPolicy;
import br.sptrans.scd.auth.domain.vo.Credentials;
import br.sptrans.scd.auth.domain.vo.DayPattern;
import br.sptrans.scd.auth.domain.vo.PersonalInfo;
import br.sptrans.scd.auth.domain.vo.TimeRange;
import br.sptrans.scd.auth.domain.vo.UserAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Agregado de domínio — usuário do sistema.
 *
 * <p>Os dados são encapsulados em Value Objects coesos:
 * <ul>
 *   <li>{@link Credentials} — login, senha, tentativas de falha, expiração</li>
 *   <li>{@link PersonalInfo} — dados pessoais (PII)</li>
 *   <li>{@link UserAudit}    — status e timestamps de auditoria</li>
 *   <li>{@link AccessPolicy} — jornada e dias permitidos</li>
 * </ul>
 *
 * <p>Os getters/setters com os nomes legados (ex.: {@code getCodLogin()}) são
 * mantidos como pontes de delegação para não quebrar callers externos.
 */
@NoArgsConstructor
public class User {

    // ── Value Objects ─────────────────────────────────────────────────────────

    @Getter @Setter
    private Long idUsuario;

    @Getter @Setter
    private Credentials credentials;

    @Getter @Setter
    private PersonalInfo personalInfo;

    @Getter @Setter
    private UserAudit audit;

    @Getter @Setter
    private AccessPolicy accessPolicy;

    // Permissões carregadas após autenticação (não persistidas junto com User)
    @Getter @Setter
    private Set<Profile> perfis = new HashSet<>();

    @Getter @Setter
    private Set<Group> grupos = new HashSet<>();

    @Getter @Setter
    private Set<Functionality> funcionalidadesDiretas = new HashSet<>();

    @Getter @Setter
    private Set<GroupUser> gruposUsuario = new HashSet<>();

    @Getter @Setter
    private Set<UserProfile> perfisUsuario = new HashSet<>();

    @Getter @Setter
    private Set<UserFunctionality> funcionalidadesUsuario = new HashSet<>();

    // ── Regras de negócio ────────────────────────────────────────────────────

    public boolean isActived() {
        return audit != null && audit.getCodStatus() != null && audit.getCodStatus().canLogin();
    }

    public boolean isBlocked() {
        return audit != null && audit.getCodStatus() != null && audit.getCodStatus().isBlocked();
    }

    public boolean isInactive() {
        return audit != null && audit.getCodStatus() == UserStatus.INACTIVE;
    }

    public void registrarTentativaFalha() {
        if (credentials == null) return;
        int novasTentativas = java.util.Objects.requireNonNullElse(credentials.getNumTentativasFalha(), 0) + 1;
        credentials = credentials.toBuilder().numTentativasFalha(novasTentativas).build();
        if (novasTentativas >= 3 && audit != null) {
            audit = audit.toBuilder().codStatus(UserStatus.BLOCKED).build();
        }
    }

    public boolean acessoPermitidoAgora() {
        if (accessPolicy == null) return true;
        return accessPolicy.isAcessoPermitidoEm(LocalDateTime.now());
    }

    public void resetarTentativas() {
        if (credentials != null) {
            credentials = credentials.toBuilder().numTentativasFalha(0).build();
        }
        if (audit != null) {
            audit = audit.toBuilder().dtUltimoAcesso(LocalDateTime.now()).build();
        }
    }

    // ── Pontes de delegação (compatibilidade com callers legados) ─────────────

    public String getCodLogin() {
        return credentials != null ? credentials.getCodLogin() : null;
    }

    public void setCodLogin(String codLogin) {
        credentials = credentials != null
                ? credentials.toBuilder().codLogin(codLogin).build()
                : Credentials.builder().codLogin(codLogin).numTentativasFalha(0).build();
    }

    public String getCodSenha() {
        return credentials != null ? credentials.getCodSenha() : null;
    }

    public void setCodSenha(String codSenha) {
        credentials = credentials != null
                ? credentials.toBuilder().codSenha(codSenha).build()
                : Credentials.builder().codSenha(codSenha).numTentativasFalha(0).build();
    }

    public String getSenhaAntiga() {
        return credentials != null ? credentials.getSenhaAntiga() : null;
    }

    public void setSenhaAntiga(String senhaAntiga) {
        credentials = credentials != null
                ? credentials.toBuilder().senhaAntiga(senhaAntiga).build()
                : Credentials.builder().senhaAntiga(senhaAntiga).numTentativasFalha(0).build();
    }

    public Integer getNumTentativasFalha() {
        return java.util.Objects.requireNonNullElse(
                credentials != null ? credentials.getNumTentativasFalha() : null, 0);
    }

    public void setNumTentativasFalha(Integer numTentativasFalha) {
        credentials = credentials != null
                ? credentials.toBuilder().numTentativasFalha(numTentativasFalha).build()
                : Credentials.builder().numTentativasFalha(numTentativasFalha).build();
    }

    public LocalDateTime getDtExpiraSenha() {
        return credentials != null ? credentials.getDtExpiraSenha() : null;
    }

    public void setDtExpiraSenha(LocalDateTime dtExpiraSenha) {
        credentials = credentials != null
                ? credentials.toBuilder().dtExpiraSenha(dtExpiraSenha).build()
                : Credentials.builder().dtExpiraSenha(dtExpiraSenha).numTentativasFalha(0).build();
    }

    // ── PersonalInfo delegation ───────────────────────────────────────────────

    public String getNomUsuario() {
        return personalInfo != null ? personalInfo.getNomUsuario() : null;
    }

    public void setNomUsuario(String nomUsuario) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().nomUsuario(nomUsuario).build()
                : PersonalInfo.builder().nomUsuario(nomUsuario).build();
    }

    public String getNomEmail() {
        return personalInfo != null ? personalInfo.getNomEmail() : null;
    }

    public void setNomEmail(String nomEmail) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().nomEmail(nomEmail).build()
                : PersonalInfo.builder().nomEmail(nomEmail).build();
    }

    public String getCodCpf() {
        return personalInfo != null ? personalInfo.getCodCpf() : null;
    }

    public void setCodCpf(String codCpf) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().codCpf(codCpf).build()
                : PersonalInfo.builder().codCpf(codCpf).build();
    }

    public String getCodRg() {
        return personalInfo != null ? personalInfo.getCodRg() : null;
    }

    public void setCodRg(String codRg) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().codRg(codRg).build()
                : PersonalInfo.builder().codRg(codRg).build();
    }

    public Long getNumTelefone() {
        return personalInfo != null ? personalInfo.getNumTelefone() : null;
    }

    public void setNumTelefone(Long numTelefone) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().numTelefone(numTelefone).build()
                : PersonalInfo.builder().numTelefone(numTelefone).build();
    }

    public String getDesEndereco() {
        return personalInfo != null ? personalInfo.getDesEndereco() : null;
    }

    public void setDesEndereco(String desEndereco) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().desEndereco(desEndereco).build()
                : PersonalInfo.builder().desEndereco(desEndereco).build();
    }

    public String getNomDepartamento() {
        return personalInfo != null ? personalInfo.getNomDepartamento() : null;
    }

    public void setNomDepartamento(String nomDepartamento) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().nomDepartamento(nomDepartamento).build()
                : PersonalInfo.builder().nomDepartamento(nomDepartamento).build();
    }

    public String getNomCargo() {
        return personalInfo != null ? personalInfo.getNomCargo() : null;
    }

    public void setNomCargo(String nomCargo) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().nomCargo(nomCargo).build()
                : PersonalInfo.builder().nomCargo(nomCargo).build();
    }

    public String getNomFuncao() {
        return personalInfo != null ? personalInfo.getNomFuncao() : null;
    }

    public void setNomFuncao(String nomFuncao) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().nomFuncao(nomFuncao).build()
                : PersonalInfo.builder().nomFuncao(nomFuncao).build();
    }

    public String getCodEmpresa() {
        return personalInfo != null ? personalInfo.getCodEmpresa() : null;
    }

    public void setCodEmpresa(String codEmpresa) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().codEmpresa(codEmpresa).build()
                : PersonalInfo.builder().codEmpresa(codEmpresa).build();
    }

    public ClassificationPerson getCodClassificacaoPessoa() {
        return personalInfo != null ? personalInfo.getCodClassificacaoPessoa() : null;
    }

    public void setCodClassificacaoPessoa(ClassificationPerson codClassificacaoPessoa) {
        personalInfo = personalInfo != null
                ? personalInfo.toBuilder().codClassificacaoPessoa(codClassificacaoPessoa).build()
                : PersonalInfo.builder().codClassificacaoPessoa(codClassificacaoPessoa).build();
    }

    // ── UserAudit delegation ──────────────────────────────────────────────────

    public UserStatus getCodStatus() {
        return audit != null ? audit.getCodStatus() : null;
    }

    public void setCodStatus(UserStatus codStatus) {
        audit = audit != null
                ? audit.toBuilder().codStatus(codStatus).build()
                : UserAudit.builder().codStatus(codStatus).build();
    }

    public LocalDateTime getDtCriacao() {
        return audit != null ? audit.getDtCriacao() : null;
    }

    public void setDtCriacao(LocalDateTime dtCriacao) {
        audit = audit != null
                ? audit.toBuilder().dtCriacao(dtCriacao).build()
                : UserAudit.builder().dtCriacao(dtCriacao).build();
    }

    public LocalDateTime getDtModi() {
        return audit != null ? audit.getDtModi() : null;
    }

    public void setDtModi(LocalDateTime dtModi) {
        audit = audit != null
                ? audit.toBuilder().dtModi(dtModi).build()
                : UserAudit.builder().dtModi(dtModi).build();
    }

    public LocalDateTime getDtUltimoAcesso() {
        return audit != null ? audit.getDtUltimoAcesso() : null;
    }

    public void setDtUltimoAcesso(LocalDateTime dtUltimoAcesso) {
        audit = audit != null
                ? audit.toBuilder().dtUltimoAcesso(dtUltimoAcesso).build()
                : UserAudit.builder().dtUltimoAcesso(dtUltimoAcesso).build();
    }

    // ── AccessPolicy delegation ───────────────────────────────────────────────

    public String getNumDiasSemanasPermitidos() {
        if (accessPolicy == null || accessPolicy.getDiasPermitidos() == null) return null;
        return accessPolicy.getDiasPermitidos().getPadrao();
    }

    public void setNumDiasSemanasPermitidos(String numDiasSemanasPermitidos) {
        if (numDiasSemanasPermitidos == null) {
            accessPolicy = null;
            return;
        }
        DayPattern dias = new DayPattern(numDiasSemanasPermitidos);
        TimeRange jornada = accessPolicy != null ? accessPolicy.getJornadaHoraria() : TimeRange.semRestricao();
        accessPolicy = new AccessPolicy(dias, jornada);
    }

    public LocalDateTime getDtJornadaIni() {
        if (accessPolicy == null || accessPolicy.getJornadaHoraria() == null) return null;
        java.time.LocalTime t = accessPolicy.getJornadaHoraria().getInicio();
        return t != null ? LocalDateTime.now().with(t) : null;
    }

    public void setDtJornadaIni(LocalDateTime dtJornadaIni) {
        java.time.LocalTime inicio = dtJornadaIni != null ? dtJornadaIni.toLocalTime() : null;
        java.time.LocalTime fim = (accessPolicy != null && accessPolicy.getJornadaHoraria() != null)
                ? accessPolicy.getJornadaHoraria().getFim() : null;
        DayPattern dias = (accessPolicy != null) ? accessPolicy.getDiasPermitidos() : DayPattern.todosDias();
        accessPolicy = new AccessPolicy(dias, new TimeRange(inicio, fim));
    }

    public LocalDateTime getDtJornadaFim() {
        if (accessPolicy == null || accessPolicy.getJornadaHoraria() == null) return null;
        java.time.LocalTime t = accessPolicy.getJornadaHoraria().getFim();
        return t != null ? LocalDateTime.now().with(t) : null;
    }

    public void setDtJornadaFim(LocalDateTime dtJornadaFim) {
        LocalTime fim = dtJornadaFim != null ? dtJornadaFim.toLocalTime() : null;
        LocalTime inicio = (accessPolicy != null && accessPolicy.getJornadaHoraria() != null)
                ? accessPolicy.getJornadaHoraria().getInicio() : null;
        DayPattern dias = (accessPolicy != null) ? accessPolicy.getDiasPermitidos() : DayPattern.todosDias();
        accessPolicy = new AccessPolicy(dias, new TimeRange(inicio, fim));
    }
}
