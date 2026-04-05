package br.sptrans.scd.channel.application.port.in;

import java.util.List;

import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;

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
            Long idUsuarioCadastro) {}

    record UpdateMarketingDistribuitionChannelCommand(
            String codStatus,
            Long idUsuarioManutencao) {}
}
