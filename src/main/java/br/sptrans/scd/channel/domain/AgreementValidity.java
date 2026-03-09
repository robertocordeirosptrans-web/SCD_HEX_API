package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementValidity {

    private AgreementValidityKey id;
    private SalesChannel canal;

    private Product produto;

    private ProductChannel canalProduto;

    private LocalDateTime dataFimValidade;

    private LocalDateTime dataInicioValidade;

    private String status;

    private LocalDateTime dataManutencao;

    private Long idUsuario;
}
