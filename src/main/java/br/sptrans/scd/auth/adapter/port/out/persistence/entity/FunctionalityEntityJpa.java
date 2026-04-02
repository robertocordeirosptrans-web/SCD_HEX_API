package br.sptrans.scd.auth.adapter.port.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "FUNCIONALIDADES", schema = "SPTRANSDBA")
public class FunctionalityEntityJpa {

    @EmbeddedId
    private FunctionalityEntityJpaKey id;

    @Column(name = "NOM_FUNCIONALIDADE", length = 100)
    private String nomFuncionalidade;

    @Column(name = "COD_STATUS", length = 1)
    private String codStatus;

    @Column(name = "DT_SINC")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MODI")
    private LocalDateTime dtManutencao;

    @Column(name = "ID_USUARIO_MANUTENCAO")
    private Long idUsuarioManutencao;

    @Column(name = "FLG_MONITORACAO", length = 1)
    private String flgMonitoracao;

    @Column(name = "FLG_EVENTO", length = 1)
    private String flgEvento;

    @PrePersist
    protected void aoInserir() {
        this.dtCadastro = LocalDateTime.now();
        this.dtManutencao = LocalDateTime.now();
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.dtManutencao = LocalDateTime.now();
    }

}
