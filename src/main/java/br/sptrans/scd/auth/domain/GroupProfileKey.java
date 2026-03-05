package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode
public class GroupProfileKey implements Serializable{

    private String codGrupo;

    private String codPerfil;

    // Default constructor
    public GroupProfileKey() {
    }

    public GroupProfileKey(String codGrupo, String codPerfil) {
        this.codGrupo = codGrupo;
        this.codPerfil = codPerfil;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }

    public String getCodPerfil() {
        return codPerfil;
    }

    public void setCodPerfil(String codPerfil) {
        this.codPerfil = codPerfil;
    }

}
