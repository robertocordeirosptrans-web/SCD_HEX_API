package br.sptrans.scd.initializedcards.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_LOTE_SCD", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TbLotSCDEntityJpa {
    @Id
    @Column(name = "ID_LOTE")
    private Long idLote;

    @Column(name = "STATUS", length = 1)
    private String status;

    @Column(name = "DT_GERACAO")
    private LocalDateTime dtGeracao;

    @Column(name = "QTD_CARTOES_LOTE")
    private Long qtdCartoesLote;

    @Column(name = "COD_TIPO_CARTAO")
    private Long codTipoCartao;
}
