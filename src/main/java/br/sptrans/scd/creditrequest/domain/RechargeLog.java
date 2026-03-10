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
}
