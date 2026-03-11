package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GroupProfileEntityJpaId implements Serializable{

    @Column(name = "COD_GRUPO")
    private String codGrupo;
    @Column(name = "COD_PERFIL")
    private String codPerfil;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GroupProfileEntityJpaId i)) {
            return false;
        
        }return Objects.equals(codGrupo, i.codGrupo) && Objects.equals(codPerfil, i.codPerfil);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codGrupo, codPerfil);
    }
}
