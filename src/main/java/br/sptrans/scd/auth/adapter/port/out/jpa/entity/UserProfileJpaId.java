package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public class UserProfileJpaId  implements Serializable{

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "COD_PERFIL")
    private String codPerfil;

    public UserProfileJpaId() {
    }

    public UserProfileJpaId(Long idUsuario, String codPerfil) {
        this.idUsuario = idUsuario;
        this.codPerfil = codPerfil;
    }

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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserProfileJpaId that = (UserProfileJpaId) o;
        return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(codPerfil, that.codPerfil);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, codPerfil);
    }
}
