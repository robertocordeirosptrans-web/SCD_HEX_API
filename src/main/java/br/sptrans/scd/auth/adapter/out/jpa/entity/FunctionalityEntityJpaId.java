package br.sptrans.scd.auth.adapter.out.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FunctionalityEntityJpaId implements Serializable{

    @Column(name = "COD_SISTEMA", length = 10)
    private String codSistema;
    @Column(name = "COD_MODULO", length = 10)
    private String codModulo;
    @Column(name = "COD_ROTINA", length = 10)
    private String codRotina;
    @Column(name = "COD_FUNCIONALIDADE", length = 30)
    private String codFuncionalidade;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FunctionalityEntityJpaId i)) {
            return false;
        }
        return Objects.equals(codSistema, i.codSistema) && Objects.equals(codModulo, i.codModulo)
                && Objects.equals(codRotina, i.codRotina) && Objects.equals(codFuncionalidade, i.codFuncionalidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codSistema, codModulo, codRotina, codFuncionalidade);
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
}
