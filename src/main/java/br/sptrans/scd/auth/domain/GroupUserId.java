package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class GroupUserId implements Serializable {

    private Long idUsuario;

    private String codGrupo;

    public GroupUserId() {
    }

    public GroupUserId(Long idUsuario, String codGrupo) {
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
