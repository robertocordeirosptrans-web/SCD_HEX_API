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

    private BigDecimal valInicio;

    private BigDecimal valFim;

    private BigDecimal valPercentual;

    private LocalDateTime dtInicio;

    private LocalDateTime dtFim;

    private LocalDateTime dtManutencao;

    private User idUsuarioManutencao;
}
