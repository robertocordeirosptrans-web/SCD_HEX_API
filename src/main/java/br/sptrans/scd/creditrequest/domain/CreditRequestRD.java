package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditRequestRD {

    private Long numSolicitacao;
    private String codCanal;
    private String codCanalDistribuicao;
    private Long idUsuarioCadastro;
    private Long idUsuarioManutencao;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
}
