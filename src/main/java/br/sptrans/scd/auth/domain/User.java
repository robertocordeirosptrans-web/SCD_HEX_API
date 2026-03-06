package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
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

    // Permissões carregadas após autenticação
    private Set<Profile> perfis = new HashSet<>();
    private Set<Group> grupos = new HashSet<>();
    private Set<Functionality> funcionalidadesDiretas = new HashSet<>();
    private Set<GroupUser> gruposUsuario;
    private Set<UserProfile> perfisUsuario;
    private Set<UserFunctionality> funcionalidadesUsuario;

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

    public void registrarTentativaFalha() {
        this.numTentativasFalha = (this.numTentativasFalha == null ? 0 : this.numTentativasFalha) + 1;
        if (this.numTentativasFalha >= 3) {
            this.codStatus = "B";
        }
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

    /**
     * Reseta contador de tentativas após login bem-sucedido.
     */
    public void resetarTentativas() {
        this.numTentativasFalha = 0;
        this.dtUltimoAcesso = LocalDateTime.now();
    }

}
