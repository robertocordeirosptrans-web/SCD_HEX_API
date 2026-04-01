package br.sptrans.scd.auth.adapter.port.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "USUARIOS", schema="SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityJpa {

    @Id
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "COD_SENHA", nullable = false)
    private String codSenha;

    @Column(name = "COD_LOGIN", nullable = false)
    private String codLogin;

    @Column(name = "COD_STATUS")
    private String codStatus;

    @Column(name = "DT_MODI")
    private LocalDateTime dtModi;

    @Column(name = "NOM_USUARIO", nullable = false)
    private String nomUsuario;

    @Column(name = "DES_ENDERECO", nullable = false)
    private String desEndereco;

    @Column(name = "NOM_DEPARTAMENTO")
    private String nomDepartamento;

    @Column(name = "NOM_CARGO")
    private String nomCargo;

    @Column(name = "NOM_FUNCAO")
    private String nomFuncao;

    @Column(name = "NUM_TELEFONE")
    private Long numTelefone;

    @Column(name = "DT_CRIACAO")
    private LocalDateTime dtCriacao;

    @Column(name = "DT_EXPIRA_SENHA")
    private LocalDateTime dtExpiraSenha;

    @Column(name = "DT_ULTIMO_ACESSO")
    private LocalDateTime dtUltimoAcesso;

    @Column(name = "COD_CPF", nullable = false)
    private String codCpf;

    @Column(name = "COD_RG", nullable = false)
    private String codRg;

    @Column(name = "NOM_EMAIL", nullable = false)
    private String nomEmail;

    @Column(name = "COD_EMPRESA")
    private String codEmpresa;

    @Column(name = "DT_JORNADA_INI")
    private LocalDateTime dt_jornada_ini;

    @Column(name = "DT_JORNADA_FIM")
    private LocalDateTime dt_jornada_fim;

    @Column(name = "COD_CLASSIFICACAO_PESSOA")
    private String codClassificacaoPessoa;

    @Column(name = "OLD_SENHA")
    private String oldSenha;

    @Column(name = "NUM_TENTATIVA")
    private Integer numTentativasFalha = 0;

    @Column(name = "NUM_DIAS_SEMANAS_PERMITIDOS")
    private String numDiasSemanasPermitidos = "7";

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<GrupoUsuarioJpaEntity> gruposUsuario;
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // private Set<UsuarioPerfilJpaEntity> perfisUsuario;
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<UsuarioFuncionalidadeJpaEntity> funcionalidadesUsuario;
}
