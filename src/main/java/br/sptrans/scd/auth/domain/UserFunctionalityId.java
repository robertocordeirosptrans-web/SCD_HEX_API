package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode
public class UserFunctionalityId {

    private String codSistema;

    private String codModulo;

    private String codRotina;

    private String codFuncionalidade;

    private Long idUsuario;

    private LocalDate dtInicioValidade;

    // Default constructor
    public UserFunctionalityId() {
    }

    public UserFunctionalityId(String codSistema, String codModulo, String codRotina, String codFuncionalidade, Long idUsuario, LocalDate dtInicioValidade) {
        this.codSistema = codSistema;
        this.codModulo = codModulo;
        this.codRotina = codRotina;
        this.codFuncionalidade = codFuncionalidade;
        this.idUsuario = idUsuario;
        this.dtInicioValidade = dtInicioValidade;
    }

    public String getCodSistema() {
        return codSistema;
    }

    public void setCodSistema(String codSistema) {
        this.codSistema = codSistema;
    }

    public String getCodModulo() {
        return codModulo;
    }

    public void setCodModulo(String codModulo) {
        this.codModulo = codModulo;
    }

    public String getCodRotina() {
        return codRotina;
    }

    public void setCodRotina(String codRotina) {
        this.codRotina = codRotina;
    }

    public String getCodFuncionalidade() {
        return codFuncionalidade;
    }

    public void setCodFuncionalidade(String codFuncionalidade) {
        this.codFuncionalidade = codFuncionalidade;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getDtInicioValidade() {
        return dtInicioValidade;
    }

    public void setDtInicioValidade(LocalDate dtInicioValidade) {
        this.dtInicioValidade = dtInicioValidade;
    }

}
