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

@Getter
@Setter
@Entity
@Table(name = "GRUPO_USUARIOS", schema = "SPTRANSDBA")
public class GroupUserEntityJpa {

    @EmbeddedId
    private GroupUserEntityJpaId id;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;
    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;
    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private UserEntityJpa usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codGrupo")
    @JoinColumn(name = "COD_GRUPO")
    private GroupEntityJpa grupo;

    @PrePersist
    protected void aoInserir() {
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }
}
