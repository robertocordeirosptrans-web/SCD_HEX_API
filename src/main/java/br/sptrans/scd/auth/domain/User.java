package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import br.sptrans.scd.auth.domain.enums.UserStatus;
import br.sptrans.scd.auth.domain.vo.AccessPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {


    private Long idUsuario;
    private String codSenha;
    private String codLogin;
    private UserStatus codStatus;
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
    private LocalDateTime dtJornadaIni;
    private LocalDateTime dtJornadaFim;
    private ClassificationPerson codClassificacaoPessoa;
    private String senhaAntiga;
    private Integer numTentativasFalha = 0;
    private String numDiasSemanasPermitidos = null;

    // Permissões carregadas após autenticação
    private Set<Profile> perfis = new HashSet<>();
    private Set<Group> grupos = new HashSet<>();
    private Set<Functionality> funcionalidadesDiretas = new HashSet<>();
    private Set<GroupUser> gruposUsuario = new HashSet<>();
    private Set<UserProfile> perfisUsuario = new HashSet<>();
    private Set<UserFunctionality> funcionalidadesUsuario = new HashSet<>();

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
        return codStatus != null && codStatus.canLogin();
    }

    public boolean isBlocked() {
        return codStatus != null && codStatus.isBlocked();
    }

    public boolean isInactive() {
        return codStatus == UserStatus.INACTIVE;
    }

    public void registrarTentativaFalha() {
        this.numTentativasFalha = (this.numTentativasFalha == null ? 0 : this.numTentativasFalha) + 1;
        if (this.numTentativasFalha >= 3) {
            this.codStatus = UserStatus.BLOCKED;
        }
    }

    /**
     * Valida se o acesso é permitido no momento atual conforme DT_JORNADA_INI,
     * DT_JORNADA_FIM e NUM_DIAS_SEMANAS_PERMITIDOS.
     */
    public boolean acessoPermitidoAgora() {
        return new AccessPolicy(
                this.numDiasSemanasPermitidos,
                this.dtJornadaIni,
                this.dtJornadaFim
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
