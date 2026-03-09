package br.sptrans.scd.channel.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.domain.Product;
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

    private User idUsuarioCadastro;

    private ProductChannel canalProduto;

    private SalesChannel canal;

    private Product produto;
}
