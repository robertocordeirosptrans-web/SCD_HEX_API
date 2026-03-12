package br.sptrans.scd.auth.domain;

import java.io.Serializable;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode
public class UserProfileId implements Serializable {
    private Long idUsuario;
    private String codPerfil;
    private java.time.LocalDateTime dtInicioValidade;
    private java.time.LocalDateTime dtFimValidade;

    public UserProfileId() {}

    public UserProfileId(Long idUsuario, String codPerfil, java.time.LocalDateTime dtInicioValidade, java.time.LocalDateTime dtFimValidade) {
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
    public java.time.LocalDateTime getDtInicioValidade() {
        return dtInicioValidade;
    }
    public void setDtInicioValidade(java.time.LocalDateTime dtInicioValidade) {
        this.dtInicioValidade = dtInicioValidade;
    }
    public java.time.LocalDateTime getDtFimValidade() {
        return dtFimValidade;
    }
    public void setDtFimValidade(java.time.LocalDateTime dtFimValidade) {
        this.dtFimValidade = dtFimValidade;
    }


}