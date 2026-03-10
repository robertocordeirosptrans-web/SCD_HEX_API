package br.sptrans.scd.creditrequest.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethod {

    private String codFormaPagto;
    private String desFormaPagto;
    private Long idUsuarioCadastro;
    private Long idUsuarioManutencao;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
}
