package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserFunctionalityEntityJpaId implements Serializable{

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
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
        if (!(o instanceof UserFunctionalityEntityJpaId i)) {
            return false;
        }
        return Objects.equals(idUsuario, i.idUsuario) && Objects.equals(codSistema, i.codSistema)
                && Objects.equals(codModulo, i.codModulo) && Objects.equals(codRotina, i.codRotina)
                && Objects.equals(codFuncionalidade, i.codFuncionalidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, codSistema, codModulo, codRotina, codFuncionalidade);
    }
}
