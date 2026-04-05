package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;

public interface MarketingDistribuitionChannelPersistencePort {
    Optional<MarketingDistribuitionChannel> findById(MarketingDistribuitionChannelKey id);

    List<MarketingDistribuitionChannel> findAll();

    /**
     * Busca CanaisComerDistrib ativo para um canal de distribuição.
     *
     * @param codCanalDistrib código do canal de distribuição
     * @return Optional com a entidade ativa se encontrada
     */
    Optional<MarketingDistribuitionChannel> findActiveByCanalDistrib(String codCanal, String codCanalDistrib);

    MarketingDistribuitionChannel save(MarketingDistribuitionChannel entity);

    void deleteById(MarketingDistribuitionChannelKey id);

    boolean existsById(MarketingDistribuitionChannelKey id);
}
