package br.sptrans.scd.auth.adapter.out.jpa.entity;

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

@Getter
@Entity
@Table(name = "PERFIL_FUNCIONALIDADES", schema = "SPTRANSDBA")
public class ProfileFunctionalityJpa {

    @EmbeddedId
    private ProfileFunctionalityJpaId id;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;
    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codPerfil")
    @JoinColumn(name = "COD_PERFIL")
    private ProfileEntityJpa perfil;

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

    public ProfileFunctionalityJpaId getId() {
        return id;
    }

    public void setId(ProfileFunctionalityJpaId id) {
        this.id = id;
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

    public ProfileEntityJpa getPerfil() {
        return perfil;
    }

    public void setPerfil(ProfileEntityJpa perfil) {
        this.perfil = perfil;
    }

    public FunctionalityEntityJpa getFuncionalidade() {
        return funcionalidade;
    }

    public void setFuncionalidade(FunctionalityEntityJpa funcionalidade) {
        this.funcionalidade = funcionalidade;
    }
}
