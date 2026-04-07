package br.sptrans.scd.channel.adapter.out.jpa.adapter;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.out.jpa.mapper.MarketingDistribuitionChannelMapper;
import br.sptrans.scd.channel.adapter.out.jpa.repository.MarketingDistribuitionChannelJpaRepository;
import br.sptrans.scd.channel.adapter.out.persistence.entity.MarketingDistribuitionChannelEntityJpa;
import br.sptrans.scd.channel.adapter.out.persistence.entity.MarketingDistribuitionChannelKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelPersistencePort;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MarketingDistribuitionChannelAdapterJpa implements MarketingDistribuitionChannelPersistencePort {

    private final MarketingDistribuitionChannelJpaRepository jpaRepository;
    private final MarketingDistribuitionChannelMapper mapper;



    @Override
    public Optional<MarketingDistribuitionChannel> findById(MarketingDistribuitionChannelKey id) {
        MarketingDistribuitionChannelKeyEntityJpa entityKey = mapper.toEntityKey(id);
        return jpaRepository.findById(entityKey)
                .map(mapper::toDomain);
    }

    @Override
    public Page<MarketingDistribuitionChannel> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }





    @Override
    public MarketingDistribuitionChannel save(MarketingDistribuitionChannel entity) {
        MarketingDistribuitionChannelEntityJpa entityJpa = mapper.toEntity(entity);
        MarketingDistribuitionChannelEntityJpa saved = jpaRepository.save(entityJpa);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(MarketingDistribuitionChannelKey id) {
        MarketingDistribuitionChannelKeyEntityJpa entityKey = mapper.toEntityKey(id);
        jpaRepository.deleteById(entityKey);
    }

    @Override
    public boolean existsById(MarketingDistribuitionChannelKey id) {
        MarketingDistribuitionChannelKeyEntityJpa entityKey = mapper.toEntityKey(id);
        return jpaRepository.existsById(entityKey);
    }

    @Override
    public Optional<MarketingDistribuitionChannel> findActiveByCanalDistrib(String codCanal, String codCanalDistrib) {
        return jpaRepository.findActiveByCanalDistribuicao(codCanal, codCanalDistrib)
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }



 
}
