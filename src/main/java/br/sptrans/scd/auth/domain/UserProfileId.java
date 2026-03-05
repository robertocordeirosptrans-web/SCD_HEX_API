package br.sptrans.scd.auth.domain;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileId that = (UserProfileId) o;
        return Objects.equals(idUsuario, that.idUsuario) &&
               Objects.equals(codPerfil, that.codPerfil);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, codPerfil);
    }
}