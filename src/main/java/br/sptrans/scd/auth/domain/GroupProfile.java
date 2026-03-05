package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

public class GroupProfile {

    private GroupProfileId id;
    private Long idUsuarioManutencao;
    private String codStatus;
    private LocalDate dtModi;
    private Group grupo;
    private Profile perfil;

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

    public LocalDate getDtModi() {
        return dtModi;
    }

    public void setDtModi(LocalDate dtModi) {
        this.dtModi = dtModi;
    }

    public Group getGrupo() {
        return grupo;
    }

    public void setGrupo(Group grupo) {
        this.grupo = grupo;
    }

    public Profile getPerfil() {
        return perfil;
    }

    public void setPerfil(Profile perfil) {
        this.perfil = perfil;
    }

    public GroupProfileId getId() {
        return id;
    }

    public void setId(GroupProfileId id) {
        this.id = id;
    }
}
