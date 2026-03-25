package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.MarketingDistribuitionChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.MarketingDistribuitionChannelKeyEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.MarketingDistribuitionChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.MarketingDistribuitionChannelJpaRepository;
import br.sptrans.scd.channel.application.port.out.MarketingDistribuitionChannelRepository;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannel;
import br.sptrans.scd.channel.domain.MarketingDistribuitionChannelKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MarketingDistribuitionChannelAdapterJpa implements MarketingDistribuitionChannelRepository {

    private final MarketingDistribuitionChannelJpaRepository jpaRepository;
    private final MarketingDistribuitionChannelMapper mapper;



    @Override
    public Optional<MarketingDistribuitionChannel> findById(MarketingDistribuitionChannelKey id) {
        MarketingDistribuitionChannelKeyEntityJpa entityKey = mapper.toEntityKey(id);
        return jpaRepository.findById(entityKey)
                .map(mapper::toDomain);
    }

    @Override
    public List<MarketingDistribuitionChannel> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<MarketingDistribuitionChannel> findByCodCanalComercializacao(String codCanalComercializacao) {
        return jpaRepository.findByCodCanalComercializacao(codCanalComercializacao)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<MarketingDistribuitionChannel> findByCodCanalDistribuicao(String codCanalDistribuicao) {
        return jpaRepository.findByCodCanalDistribuicao(codCanalDistribuicao)
                .stream().map(mapper::toDomain).toList();
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
    public Optional<MarketingDistribuitionChannel> findActiveByCanalDistrib(String codCanalDistrib) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findActiveByCanalDistrib'");
    }

    @Override
    public Optional<MarketingDistribuitionChannel> findByIdOtimized(String codCanal, String codProduto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByIdOtimized'");
    }

 
}
