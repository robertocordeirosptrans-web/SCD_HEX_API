package br.sptrans.scd.auth.adapter.port.out.persistence.entity;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "GRUPO_PERFIS", schema = "SPTRANSDBA")
public class GroupProfileEntityJpa {

    @EmbeddedId
    private GroupProfileEntityJpaId id;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;


    @Column(name = "DT_MODI")
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
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

 

}
