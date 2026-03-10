package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;

public interface MarketingDistribuitionChannelRepository {

    Optional<MarketingDistribuitionChannel> findById(MarketingDistribuitionChannelKey id);

    List<MarketingDistribuitionChannel> findAll();

    List<MarketingDistribuitionChannel> findByCodCanalComercializacao(String codCanalComercializacao);

    List<MarketingDistribuitionChannel> findByCodCanalDistribuicao(String codCanalDistribuicao);

    MarketingDistribuitionChannel save(MarketingDistribuitionChannel entity);

    void deleteById(MarketingDistribuitionChannelKey id);

    boolean existsById(MarketingDistribuitionChannelKey id);
}
