package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.auth.domain.User;

public interface MarketingDistribuitionChannelUseCase {

    MarketingDistribuitionChannel createMarketingDistribuitionChannel(CreateMarketingDistribuitionChannelCommand command);

    MarketingDistribuitionChannel updateMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao, UpdateMarketingDistribuitionChannelCommand command);

    MarketingDistribuitionChannel findMarketingDistribuitionChannel(String codCanalComercializacao, String codCanalDistribuicao);

    List<MarketingDistribuitionChannel> findAllMarketingDistribuitionChannels();



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
