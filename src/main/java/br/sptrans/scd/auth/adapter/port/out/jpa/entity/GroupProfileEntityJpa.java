package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "GRUPO_PERFIS", schema = "SPTRANSDBA")
public class GroupProfileEntityJpa {

    @EmbeddedId
    private GroupProfileEntityJpaId id;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;
    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codGrupo")
    @JoinColumn(name = "COD_GRUPO")
    private GroupEntityJpa grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codPerfil")
    @JoinColumn(name = "COD_PERFIL")
    private ProfileEntityJpa perfil;

    @PrePersist
    protected void aoInserir() {
        this.dtCadastro = LocalDateTime.now();
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

    public GroupProfileEntityJpaId getId() {
        return id;
    }

    public void setId(GroupProfileEntityJpaId id) {
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

    public GroupEntityJpa getGrupo() {
        return grupo;
    }

    public void setGrupo(GroupEntityJpa grupo) {
        this.grupo = grupo;
    }

    public ProfileEntityJpa getPerfil() {
        return perfil;
    }

    public void setPerfil(ProfileEntityJpa perfil) {
        this.perfil = perfil;
    }

}
