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

    private LocalDateTime desOcorrencia;

    private User idUsuarioTransicao;
}
