package br.sptrans.scd.channel.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RechargeLimit {

    private RechargeLimitKey id;

    private LocalDateTime dtInicioValidade;
    private LocalDateTime dtFimValidade;
    private BigDecimal vlMinimoRecarga;
    private BigDecimal vlMaximoRecarga;
    private BigDecimal vlMaximoSaldo;
    private String codStatus;
    private LocalDateTime dtManutencao;

    private Long idUsuarioCadastro;
}
