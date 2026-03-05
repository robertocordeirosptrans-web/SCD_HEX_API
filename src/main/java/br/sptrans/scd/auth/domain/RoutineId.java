package br.sptrans.scd.auth.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoutineId {

    private String codSistema;

    private String codModulo;

    private String codRotina;

    // Getters and setters
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
}
