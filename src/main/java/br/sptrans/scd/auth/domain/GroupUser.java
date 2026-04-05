package br.sptrans.scd.auth.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupUser {
    
    private GroupUserKey id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDateTime dtModi;
    private User usuario;
    private Group grupo;

    public void setId(GroupUserKey id) {
        this.id = id;
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

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }


}