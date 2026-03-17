package br.sptrans.scd.initializedcards.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TbLotSCD {

    private Long idLote;
    private String status;
    private LocalDateTime dtGeracao;
    private Long qtdCartoesLote;
    private Long codTipoCartao;
}
