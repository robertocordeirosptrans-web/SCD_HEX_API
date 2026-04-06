package br.sptrans.scd.auth.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USUARIO_FUNCIONALIDADES", schema = "SPTRANSDBA")
@Getter
@Setter
public class UserFunctionalityEntityJpa {

    @EmbeddedId
    private UserFunctionalityEntityJpaId id;

    @Column(name = "COD_STATUS_USU_FUN", length = 1)
    private String codStatusUsuFun;   // "A" = Ativo  "I" = Inativo

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;
    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private UserEntityJpa usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "COD_SISTEMA", referencedColumnName = "COD_SISTEMA", insertable = false, updatable = false),
        @JoinColumn(name = "COD_MODULO", referencedColumnName = "COD_MODULO", insertable = false, updatable = false),
        @JoinColumn(name = "COD_ROTINA", referencedColumnName = "COD_ROTINA", insertable = false, updatable = false),
        @JoinColumn(name = "COD_FUNCIONALIDADE", referencedColumnName = "COD_FUNCIONALIDADE", insertable = false, updatable = false)
    })
    private FunctionalityEntityJpa funcionalidade;

    @PrePersist
    protected void aoInserir() {
        this.dtCadastro = LocalDateTime.now();
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

    public UserFunctionalityEntityJpaId getId() {
        return id;
    }

    public void setId(UserFunctionalityEntityJpaId id) {
        this.id = id;
    }

    public String getCodStatusUsuFun() {
        return codStatusUsuFun;
    }

    public void setCodStatusUsuFun(String codStatusUsuFun) {
        this.codStatusUsuFun = codStatusUsuFun;
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

    public UserEntityJpa getUsuario() {
        return usuario;
    }

    public void setUsuario(UserEntityJpa usuario) {
        this.usuario = usuario;
    }

    public FunctionalityEntityJpa getFuncionalidade() {
        return funcionalidade;
    }

    public void setFuncionalidade(FunctionalityEntityJpa funcionalidade) {
        this.funcionalidade = funcionalidade;
    }
}
