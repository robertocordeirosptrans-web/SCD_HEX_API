package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserProfile {

    private UserProfileId id;

    private Long idUsuarioManutencao;

    private String codStatus;

    private LocalDateTime dtModi;

    private LocalDateTime dtInicioValidade;

    private User user;

    private Profile perfil;

    public Long getIdUsuario() {
        return id.getIdUsuario();
    }

    public void setIdUsuario(Long idUsuario) {
        this.id.setIdUsuario(idUsuario);
    }

    public String getCodPerfil() {
        return id.getCodPerfil();
    }

    public void setCodPerfil(String codPerfil) {
        this.id.setCodPerfil(codPerfil);
    }

    public Long getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public void setIdUsuarioManutencao(Long idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public LocalDateTime getDtModi() {
        return dtModi;
    }

    public void setDtModi(LocalDateTime dtModi) {
        this.dtModi = dtModi;
    }

    public LocalDateTime getDtInicioValidade() {
        return dtInicioValidade;
    }

    public void setDtInicioValidade(LocalDateTime dtInicioValidade) {
        this.dtInicioValidade = dtInicioValidade;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}