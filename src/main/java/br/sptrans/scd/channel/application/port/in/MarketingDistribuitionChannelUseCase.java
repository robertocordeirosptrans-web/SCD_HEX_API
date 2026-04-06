package br.sptrans.scd.channel.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;

public interface MarketingDistribuitionChannelUseCase {

    MarketingDistribuitionChannel createMarketingDistribuitionChannel(CreateMarketingDistribuitionChannelCommand command);

    MarketingDistribuitionChannel updateMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao, UpdateMarketingDistribuitionChannelCommand command);

    MarketingDistribuitionChannel findMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao);

    Page<MarketingDistribuitionChannel> findAllMarketingDistribuitionChannels(Pageable pageable);



    void deleteMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateMarketingDistribuitionChannelCommand(
            String codCanalComercializacao,
            String codCanalDistribuicao,
            String codStatus,
            User usuarioCadastro) {}

    record UpdateMarketingDistribuitionChannelCommand(
            String codStatus,
            User usuarioManutencao) {}
}
