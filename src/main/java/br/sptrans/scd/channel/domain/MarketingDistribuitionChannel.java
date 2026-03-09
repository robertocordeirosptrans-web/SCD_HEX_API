package br.sptrans.scd.channel.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketingDistribuitionChannel {

    private MarketingDistribuitionChannelKey id;
    
    private String codStatus;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;

    private String codCanalComercializacao;

    private String codCanalDistribuicao;
}
