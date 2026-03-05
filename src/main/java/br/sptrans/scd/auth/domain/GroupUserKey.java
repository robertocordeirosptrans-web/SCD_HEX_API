package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class GroupUserKey implements Serializable {

    private Long idUsuario;

    private String codGrupo;

    public GroupUserKey() {
    }

    public GroupUserKey(Long idUsuario, String codGrupo) {
        this.idUsuario = idUsuario;
        this.codGrupo = codGrupo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }
}
