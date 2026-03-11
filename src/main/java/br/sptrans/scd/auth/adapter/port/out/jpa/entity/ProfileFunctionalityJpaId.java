package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProfileFunctionalityJpaId implements Serializable {

    @Column(name = "COD_PERFIL")
    private String codPerfil;
    @Column(name = "COD_SISTEMA")
    private String codSistema;
    @Column(name = "COD_MODULO")
    private String codModulo;
    @Column(name = "COD_ROTINA")
    private String codRotina;
    @Column(name = "COD_FUNCIONALIDADE")
    private String codFuncionalidade;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProfileFunctionalityJpaId i)) {
            return false;
        }
        return Objects.equals(codPerfil, i.codPerfil) && Objects.equals(codSistema, i.codSistema)
                && Objects.equals(codModulo, i.codModulo) && Objects.equals(codRotina, i.codRotina)
                && Objects.equals(codFuncionalidade, i.codFuncionalidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codPerfil, codSistema, codModulo, codRotina, codFuncionalidade);
    }

    public String getCodPerfil() {
        return codPerfil;
    }

    public void setCodPerfil(String v) {
        this.codPerfil = v;
    }

    public String getCodSistema() {
        return codSistema;
    }

    public void setCodSistema(String v) {
        this.codSistema = v;
    }

    public String getCodModulo() {
        return codModulo;
    }

    public void setCodModulo(String v) {
        this.codModulo = v;
    }

    public String getCodRotina() {
        return codRotina;
    }

    public void setCodRotina(String v) {
        this.codRotina = v;
    }

    public String getCodFuncionalidade() {
        return codFuncionalidade;
    }

    public void setCodFuncionalidade(String v) {
        this.codFuncionalidade = v;
    }
}
