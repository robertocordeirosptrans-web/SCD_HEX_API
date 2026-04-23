package br.sptrans.scd.initializedcards.adapter.in.rest.response;

import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteDisponivelResponse {

    private Long idLote;
    private Long qtdCartoesLote;
    private String flgAssocia;

    public static LoteDisponivelResponse fromDomain(TbLotSCD domain) {
        return LoteDisponivelResponse.builder()
                .idLote(domain.getIdLote())
                .qtdCartoesLote(domain.getQtdCartoesLote())
                .flgAssocia("A")
                .build();
    }
}
