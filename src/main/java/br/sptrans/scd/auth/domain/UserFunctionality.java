package br.sptrans.scd.auth.domain;

import java.time.LocalDate;

public class UserFunctionality {

    private UserFunctionalityId id;
    private LocalDate dtFimValidade;
    private Long idUsuarioManutencao;
    private LocalDate dtModi;
    private String codStatusUsuFun;
    private Functionality funcionalidade;
    private User user;

    public UserFunctionalityId getId() {
        return id;
    }

    public void setId(UserFunctionalityId id) {
        this.id = id;
    }

    public LocalDate getDtFimValidade() {
        return dtFimValidade;
    }

    public void setDtFimValidade(LocalDate dtFimValidade) {
        this.dtFimValidade = dtFimValidade;
    }

    public Long getIdUsuarioManutencao() {
        return idUsuarioManutencao;
    }

    public void setIdUsuarioManutencao(Long idUsuarioManutencao) {
        this.idUsuarioManutencao = idUsuarioManutencao;
    }

    public LocalDate getDtModi() {
        return dtModi;
    }

    public void setDtModi(LocalDate dtModi) {
        this.dtModi = dtModi;
    }

    public String getCodStatusUsuFun() {
        return codStatusUsuFun;
    }

    public void setCodStatusUsuFun(String codStatusUsuFun) {
        this.codStatusUsuFun = codStatusUsuFun;
    }

    public Functionality getFuncionalidade() {
        return funcionalidade;
    }

    public void setFuncionalidade(Functionality funcionalidade) {
        this.funcionalidade = funcionalidade;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
