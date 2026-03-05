package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class FunctionalityKey implements Serializable {

    public FunctionalityKey() {
    }

    public FunctionalityKey(String codSistema, String codModulo, String codRotina, String codFuncionalidade) {
        this.codSistema = codSistema;
        this.codModulo = codModulo;
        this.codRotina = codRotina;
        this.codFuncionalidade = codFuncionalidade;
    }

    private String codSistema;
    private String codModulo;
    private String codRotina;
    private String codFuncionalidade;

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
}
