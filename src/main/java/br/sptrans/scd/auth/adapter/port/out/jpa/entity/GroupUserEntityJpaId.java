package br.sptrans.scd.auth.adapter.out.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GroupUserEntityJpaId implements Serializable {

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
    @Column(name = "COD_GRUPO")
    private String codGrupo;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GroupUserEntityJpaId i)) {
            return false;
        
        }return Objects.equals(idUsuario, i.idUsuario) && Objects.equals(codGrupo, i.codGrupo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, codGrupo);
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long v) {
        this.idUsuario = v;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public void setCodGrupo(String v) {
        this.codGrupo = v;
    }
}
