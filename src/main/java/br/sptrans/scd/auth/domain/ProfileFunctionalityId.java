package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ProfileFunctionalityId implements Serializable {

    private String codSistema;

    private String codModulo;

    private String codRotina;

    private String codFuncionalidade;

    private String codPerfil;

    // Default constructor
    public ProfileFunctionalityId() {
    }

    public ProfileFunctionalityId(String codSistema, String codModulo, String codRotina, String codFuncionalidade, String codPerfil) {
        this.codSistema = codSistema;
        this.codModulo = codModulo;
        this.codRotina = codRotina;
        this.codFuncionalidade = codFuncionalidade;
        this.codPerfil = codPerfil;
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

    public String getCodPerfil() {
        return codPerfil;
    }

    public void setCodPerfil(String codPerfil) {
        this.codPerfil = codPerfil;
    }

}
