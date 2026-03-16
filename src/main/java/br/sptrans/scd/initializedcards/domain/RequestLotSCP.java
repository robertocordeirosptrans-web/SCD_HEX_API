package br.sptrans.scd.initializedcards.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLotSCP {

    private String codTipoCanal;
    private String codCanal;
    private Long nrSolicitacao;
    private Long idLote;
    private String flgFaseSolicitacao;
    private Long qtdProduto;
    private String stSolicitacaoLoteSCP;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;
}
