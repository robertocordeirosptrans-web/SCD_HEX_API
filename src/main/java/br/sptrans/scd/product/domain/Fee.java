package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.channel.domain.SalesChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fee {

    private Long codTaxa;

    private LocalDateTime dtInicio;

    private String desTaxa;

    private LocalDateTime dtFinal;

    private String codCanal;

    private String codProduto;

    private SalesChannel canal;

    private Product produto;


     // Relacionamento 1:1 com TaxasAdm
    private AdministrativeFee taxaAdministrativa;

    // Relacionamento 1:1 com TaxasServico
    private ServiceFee taxaServico;

    // Relacionamento 1:1 com TaxasDes (se necessário)
    private DestinyFee taxaDes;

    public void update(String desTaxa, LocalDateTime dtFinal) {
        this.desTaxa = desTaxa;
        this.dtFinal = dtFinal;
    }

}
