package br.sptrans.scd.initializedcards.domain;

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
public class RequestLotSCP {

    private RequestLotSCPKey id;
    private Long qtdProduto;
    private String stSolicitacaoLoteSCP;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;
}
