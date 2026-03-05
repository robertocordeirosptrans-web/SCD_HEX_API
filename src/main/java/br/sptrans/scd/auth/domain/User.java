package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
public class User {

    private Long idUsuario;
    private String codSenha;
    private String codLogin;
    private String codStatus;
    private LocalDateTime dtModi;
    private String nomUsuario;
    private String desEndereco;
    private String nomDepartamento;
    private String nomCargo;
    private String nomFuncao;
    private Long numTelefone;
    private LocalDateTime dtCriacao;
    private LocalDateTime dtExpiraSenha;
    private LocalDateTime dtUltimoAcesso;
    private String codCpf;
    private String codRg;
    private String nomEmail;
    private String codEmpresa;
    private Date dt_jornada_ini;
    private Date dt_jornada_fim;
    private ClassificationPerson codClassificacaoPessoa;
    private String newSenha;
    private String oldSenha;
    private Integer numTentativasFalha = 0;
    private String numDiasSemanasPermitidos = "7";
    private Set<GroupUser> gruposUsuario;
    private Set<UserProfile> perfisUsuario;
    private Set<UserFunctionality> funcionalidadesUsuario;

    // ── Regras de negócio ────────────────────────────────────────────────────
    /**
     * Conta está disponível para login.
     */
    public boolean isActived() {
        return "A".equalsIgnoreCase(this.codStatus);
    }

    /**
     * Conta foi bloqueada (3 tentativas ou bloqueio manual).
     */
    public boolean isBlocked() {
        return "B".equalsIgnoreCase(this.codStatus);
    }

    public boolean isInactive() {
        return "I".equalsIgnoreCase(this.codStatus);
    }

    /**
     * Registra falha de login. Bloqueia automaticamente ao atingir 3 tentativas
     * consecutivas.
     */
    public void registrarTentativaFalha() {
        this.numTentativasFalha = (this.numTentativasFalha == null ? 0 : this.numTentativasFalha) + 1;
        if (this.numTentativasFalha >= 3) {
            this.codStatus = "B";
        }
    }

    /**
     * Reseta contador de tentativas após login bem-sucedido.
     */
    public void resetarTentativas() {
        this.numTentativasFalha = 0;
        this.dtUltimoAcesso = LocalDateTime.now();
    }

    /**
     * Valida se o acesso é permitido no momento atual conforme DT_JORNADA_INI,
     * DT_JORNADA_FIM e NUM_DIAS_SEMANAS_PERMITIDOS.
     */
    public boolean acessoPermitidoAgora() {
        return new AccessPolicy(
                this.numDiasSemanasPermitidos,
                this.dt_jornada_ini,
                this.dt_jornada_fim
        ).validarAcesso();
    }

    public User(String codLogin, String codSenha, String nomUsuario, String desEndereco, String nomDepartamento, String nomCargo, String nomFuncao, String codCpf, String codRg, String nomEmail, ClassificationPerson codClassificacaoPessoa, String codEmpresa) {
        this.codLogin = codLogin;
        this.codSenha = codSenha;
        this.nomUsuario = nomUsuario;
        this.desEndereco = desEndereco;
        this.nomDepartamento = nomDepartamento;
        this.nomCargo = nomCargo;
        this.nomFuncao = nomFuncao;
        this.codCpf = codCpf;
        this.codRg = codRg;
        this.nomEmail = nomEmail;
        this.codClassificacaoPessoa = codClassificacaoPessoa;
        this.codEmpresa = codEmpresa;
    }

    public String getCodLogin() {
        return codLogin;
    }

    public void setCodLogin(String codLogin) {
        this.codLogin = codLogin;
    }

    public String getCodSenha() {
        return codSenha;
    }

    public void setCodSenha(String codSenha) {
        this.codSenha = codSenha;
    }

    public String getNomUsuario() {
        return nomUsuario;
    }

    public void setNomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    public String getDesEndereco() {
        return desEndereco;
    }

    public void setDesEndereco(String desEndereco) {
        this.desEndereco = desEndereco;
    }

    public String getNomDepartamento() {
        return nomDepartamento;
    }

    public void setNomDepartamento(String nomDepartamento) {
        this.nomDepartamento = nomDepartamento;
    }

    public String getNomCargo() {
        return nomCargo;
    }

    public void setNomCargo(String nomCargo) {
        this.nomCargo = nomCargo;
    }

    public String getNomFuncao() {
        return nomFuncao;
    }

    public void setNomFuncao(String nomFuncao) {
        this.nomFuncao = nomFuncao;
    }

    public Long getNumTelefone() {
        return numTelefone;
    }

    public void setNumTelefone(Long numTelefone) {
        this.numTelefone = numTelefone;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public LocalDateTime getDtModi() {
        return dtModi;
    }

    public void setDtModi(LocalDateTime dtModi) {
        this.dtModi = dtModi;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(LocalDateTime dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public LocalDateTime getDtExpiraSenha() {
        return dtExpiraSenha;
    }

    public void setDtExpiraSenha(LocalDateTime dtExpiraSenha) {
        this.dtExpiraSenha = dtExpiraSenha;
    }

    public LocalDateTime getDtUltimoAcesso() {
        return dtUltimoAcesso;
    }

    public void setDtUltimoAcesso(LocalDateTime dtUltimoAcesso) {
        this.dtUltimoAcesso = dtUltimoAcesso;
    }

    public String getCodCpf() {
        return codCpf;
    }

    public String getCodRg() {
        return codRg;
    }

    public String getNomEmail() {
        return nomEmail;
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodCpf(String codCpf) {
        this.codCpf = codCpf;
    }

    public void setCodRg(String codRg) {
        this.codRg = codRg;
    }

    public void setNomEmail(String nomEmail) {
        this.nomEmail = nomEmail;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getOldSenha() {
        return oldSenha;
    }

    public void setOldSenha(String oldSenha) {
        this.oldSenha = oldSenha;
    }

    public String getNewSenha() {
        return newSenha;
    }

    public void setNewSenha(String newSenha) {
        this.newSenha = newSenha;
    }

    public ClassificationPerson getCodClassificacaoPessoa() {
        return codClassificacaoPessoa;
    }

    public void setCodClassificacaoPessoa(ClassificationPerson codClassificacaoPessoa) {
        this.codClassificacaoPessoa = codClassificacaoPessoa;
    }

    public Integer getNumTentativasFalha() {
        return numTentativasFalha;
    }

    public void setNumTentativasFalha(Integer numTentativasFalha) {
        this.numTentativasFalha = numTentativasFalha;
    }

    public String getNumDiasSemanasPermitidos() {
        return numDiasSemanasPermitidos;
    }

    public void setNumDiasSemanasPermitidos(String numDiasSemanasPermitidos) {
        this.numDiasSemanasPermitidos = numDiasSemanasPermitidos;
    }

    public Date getDt_jornada_ini() {
        return dt_jornada_ini;
    }

    public void setDt_jornada_ini(Date dt_jornada_ini) {
        this.dt_jornada_ini = dt_jornada_ini;
    }

    public Date getDt_jornada_fim() {
        return dt_jornada_fim;
    }

    public void setDt_jornada_fim(Date dt_jornada_fim) {
        this.dt_jornada_fim = dt_jornada_fim;
    }

}
