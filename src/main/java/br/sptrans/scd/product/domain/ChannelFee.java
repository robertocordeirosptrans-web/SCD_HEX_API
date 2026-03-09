package br.sptrans.scd.product.domain;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelFee {
    private ChannelFeeKey id;

    private BigDecimal vltInicio;

    private BigDecimal vltFinal;

    private BigDecimal vlPercentual;

    private LocalDateTime dtInicio;

    private LocalDateTime dtFinal;

    private LocalDateTime dtManutencao;

    private User idUsuarioManutencao;
}
