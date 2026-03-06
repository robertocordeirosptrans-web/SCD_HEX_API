package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode
public class UserProfileId implements Serializable {
    private Long idUsuario;

    private String codPerfil;

    // Default constructor
    public UserProfileId() {}

    public UserProfileId(Long idUsuario, String codPerfil) {
        this.idUsuario = idUsuario;
        this.codPerfil = codPerfil;
    }

    // Getters and setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodPerfil() {
        return codPerfil;
    }

    public void setCodPerfil(String codPerfil) {
        this.codPerfil = codPerfil;
    }


}