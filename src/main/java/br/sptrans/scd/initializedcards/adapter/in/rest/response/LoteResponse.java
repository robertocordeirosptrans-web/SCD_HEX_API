package br.sptrans.scd.initializedcards.adapter.in.rest.response;

import java.time.LocalDateTime;

import br.sptrans.scd.initializedcards.domain.RequestLotSCP;
import br.sptrans.scd.initializedcards.domain.TbLotSCD;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteResponse {

    private Long idLote;
    private Long qtdProduto;
    private String stSolicitacaoLoteScp;
    private String status;
    private LocalDateTime dtGeracao;
    private Long qtdCartoesLote;
    private Long codTipoCartao;

    public static LoteResponse fromDomain(RequestLotSCP lotSCP, TbLotSCD tbLot) {
        return LoteResponse.builder()
                .idLote(lotSCP.getId() != null ? lotSCP.getId().getIdLote() : null)
                .qtdProduto(lotSCP.getQtdProduto())
                .stSolicitacaoLoteScp(lotSCP.getStSolicitacaoLoteSCP())
                .status(tbLot != null ? tbLot.getStatus() : null)
                .dtGeracao(tbLot != null ? tbLot.getDtGeracao() : null)
                .qtdCartoesLote(tbLot != null ? tbLot.getQtdCartoesLote() : null)
                .codTipoCartao(tbLot != null ? tbLot.getCodTipoCartao() : null)
                .build();
    }
}
