package br.sptrans.scd.creditrequest.adapter.out.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "HIST_SOL_ITEM_SITUACOES", schema = "SPTRANSDBA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistCreditRequestItemsEJpa {

    @EmbeddedId
    private HistCreditRequestItemsKeyEJpa id;

    @Column(name = "COD_TIPO_DOCUMENTO", length = 20)
    private String codTipoDocumento;

    @Column(name = "COD_SITUACAO", length = 2)
    private String codSituacao;

    @Column(name = "DT_TRANSICAO")
    private LocalDateTime dtTransicao;

    @Column(name = "ID_ORIGEM_TRANSICAO", length = 50)
    private String idOrigemTransicao;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro;

    @Column(name = "DT_MANUTENCAO")
    private LocalDateTime dtManutencao;

    @Column(name = "DT_PAGTO_ECONOMICA")
    private LocalDateTime dtPgtoEconomica;

    @Column(name = "SQ_PID")
    private Long sqPID;

    @Column(name = "DT_INIC_PROCESSO")
    private LocalDateTime dtInicProcesso;

    @Column(name = "DT_FIM_PROCESSO")
    private LocalDateTime dtFimProcesso;

    @Column(name = "DES_OCORRENCIA", length = 200)
    private String desOcorrencia;

    @Column(name = "ID_USUARIO_TRANSICAO")
    private Long idUsuarioTransicao;

}
