package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserProfileJpaId implements Serializable {

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "COD_PERFIL")
    private String codPerfil;

    @Column(name = "DT_INICIO_VALIDADE")
    private LocalDateTime dtInicioValidade;

    @Column(name = "DT_FIM_VALIDADE")
    private LocalDateTime dtFimValidade;

    public UserProfileJpaId() {}

    public UserProfileJpaId(Long idUsuario, String codPerfil, LocalDateTime dtInicioValidade, LocalDateTime dtFimValidade) {
        this.idUsuario = idUsuario;
        this.codPerfil = codPerfil;
        this.dtInicioValidade = dtInicioValidade;
        this.dtFimValidade = dtFimValidade;
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
    public LocalDateTime getDtInicioValidade() {
        return dtInicioValidade;
    }
    public void setDtInicioValidade(LocalDateTime dtInicioValidade) {
        this.dtInicioValidade = dtInicioValidade;
    }
    public LocalDateTime getDtFimValidade() {
        return dtFimValidade;
    }
    public void setDtFimValidade(LocalDateTime dtFimValidade) {
        this.dtFimValidade = dtFimValidade;
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
