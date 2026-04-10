package br.sptrans.scd.channel.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;

public interface MarketingDistribuitionChannelPersistencePort {
    Optional<MarketingDistribuitionChannel> findById(MarketingDistribuitionChannelKey id);

    Page<MarketingDistribuitionChannel> findAll(Pageable pageable);

    /**
     * Busca CanaisComerDistrib ativo para um canal de distribuição.
     *
     * @param codCanalDistrib código do canal de distribuição
     * @return Optional com a entidade ativa se encontrada
     */
    Optional<MarketingDistribuitionChannel> findActiveByCanalDistrib(String codCanal, String codCanalDistrib);

    Optional<MarketingDistribuitionChannel> findByAssocied(String codCanal);



    MarketingDistribuitionChannel save(MarketingDistribuitionChannel entity);

    void deleteById(MarketingDistribuitionChannelKey id);

    boolean existsById(MarketingDistribuitionChannelKey id);
}
