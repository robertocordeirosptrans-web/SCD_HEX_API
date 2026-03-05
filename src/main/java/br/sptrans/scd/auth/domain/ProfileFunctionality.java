package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

public class ProfileFunctionality {

    private ProfileFunctionalityKey id;
    private Long idUsuarioManutencao;
    private LocalDate dtInicioValidade;
    private Functionality funcionalidade;
    private Profile perfil;

    public Long getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public void setIdUsuarioManutencao(Long idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public LocalDate getDtInicioValidade() {
        return dtInicioValidade;
    }

    public void setDtInicioValidade(LocalDate dtInicioValidade) {
        this.dtInicioValidade = dtInicioValidade;
    }

    public Functionality getFuncionalidade() {
        return funcionalidade;
    }

    public void setFuncionalidade(Functionality funcionalidade) {
        this.funcionalidade = funcionalidade;
    }

    public Profile getPerfil() {
        return perfil;
    }

    public void setPerfil(Profile perfil) {
        this.perfil = perfil;
    }

    public ProfileFunctionalityKey getId() {
        return id;
    }

    public void setId(ProfileFunctionalityKey id) {
        this.id = id;
    }
}
