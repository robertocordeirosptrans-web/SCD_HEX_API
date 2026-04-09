
package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistCreditRequestItems {

    private HistCreditRequestItemsKey id;

    private String codTipoDocumento;

    private String codSituacao;

    private LocalDateTime dtTransicao;

    private String idOrigemTransicao;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private LocalDateTime dtPgtoEconomica;

    private Long sqPID;

    private LocalDateTime dtInicProcesso;

    private LocalDateTime dtFimProcesso;

    private String desOcorrencia;

    private User idUsuarioTransicao;

    /**
     * Método utilitário para criar histórico a partir de um item.
     */
    public static HistCreditRequestItems from(CreditRequestItems item, Long seqHistSdis, String origemTransicao,
            User usuario) {
        return item.criarHistorico(seqHistSdis, origemTransicao, usuario);
    }
}
