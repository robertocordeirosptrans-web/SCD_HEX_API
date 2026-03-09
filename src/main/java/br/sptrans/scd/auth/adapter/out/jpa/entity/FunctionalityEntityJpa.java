package br.sptrans.scd.auth.adapter.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "FUNCIONALIDADES_BKP_MGR", schema = "SPTRANSDBA")
public class FunctionalityEntityJpa {

    @EmbeddedId
    private FunctionalityEntityJpaKey id;

    @Column(name = "NOM_FUNCIONALIDADE", length = 100)
    private String nomFuncionalidade;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;
    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @PrePersist
    protected void aoInserir() {
        this.dtCadastro = LocalDateTime.now();
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

    public String getNomFuncionalidade() {
        return nomFuncionalidade;
    }

    public void setNomFuncionalidade(String nomFuncionalidade) {
        this.nomFuncionalidade = nomFuncionalidade;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public LocalDateTime getDtManutencao() {
        return dtManutencao;
    }

    public void setDtManutencao(LocalDateTime dtManutencao) {
        this.dtManutencao = dtManutencao;
    }

    public FunctionalityEntityJpaKey getId() {
        return id;
    }

    public void setId(FunctionalityEntityJpaKey id) {
        this.id = id;
    }
}
