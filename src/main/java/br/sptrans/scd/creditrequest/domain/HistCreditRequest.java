package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistCreditRequest {

    private HistCreditRequestKey id;

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

    private LocalDateTime desOcorrencia;

    private User idUsuarioTransicao;

    /**
     * Método utilitário para criar histórico a partir de uma solicitação.
     */
    public static HistCreditRequest from(CreditRequest request, Long seqHistSdis, String origemTransicao,
            User usuario) {
        return request.criarHistorico(seqHistSdis, origemTransicao, usuario);
    }
}
