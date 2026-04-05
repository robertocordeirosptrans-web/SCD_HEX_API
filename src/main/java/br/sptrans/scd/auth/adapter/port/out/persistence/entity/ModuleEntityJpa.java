package br.sptrans.scd.auth.adapter.port.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "MODULOS", schema = "SPTRANSDBA")
public class ModuleEntityJpa {

    @EmbeddedId
    private ModuleEntityJpaKey id;

    @Column(name = "NOM_MODULO" , length = 60)
    private LocalDateTime nomModulo;

    @Column(name = "NOM_EXECUTAVEL", length = 256)
    private LocalDateTime nomExecutavel;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;
}
