package br.sptrans.scd.auth.adapter.port.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "ROTINAS", schema = "SPTRANSDBA")
public class RoutineEntityJpa {

    @EmbeddedId
    private RoutineEntityJpaKey id;

    @Column(name = "NOM_ROTINA", length = 60)
    private String nomRotina;

    @Column(name = "NOM_LINK", length = 60)
    private String nomLink;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

}
