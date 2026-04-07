package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RechargeLog {

    private String numLogicoCartao;
    private Integer seqRecarga;
    private LocalDateTime dtSolicRecarga;
    private LocalDateTime dtCadastro;
    private Long idUsuarioCadastro;
    private LocalDateTime dtManutencao;
    private Long idUsuarioManutencao;

    /**
     * Incrementa o número de sequência da recarga.
     */
    public void incrementarSeqRecarga() {
        if (this.seqRecarga == null)
            this.seqRecarga = 1;
        else
            this.seqRecarga++;
    }

    /**
     * Atualiza datas de cadastro e manutenção para o momento atual.
     */
    public void atualizarDatas() {
        this.dtCadastro = java.time.LocalDateTime.now();
        this.dtManutencao = java.time.LocalDateTime.now();
    }

    /**
     * Valida se a data de solicitação de recarga é anterior à data atual.
     */
    public boolean isDtSolicRecargaValida() {
        return this.dtSolicRecarga != null && !this.dtSolicRecarga.isAfter(java.time.LocalDateTime.now());
    }
}
