package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.RechargeLimitMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.RechargeLimitJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.RechargeLimitKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.RechargeLimitPersistencePort;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RechargeLimitAdapterJpa implements RechargeLimitPersistencePort {

    private final RechargeLimitJpaRepository rechargeLimitJpaRepository;
    private final RechargeLimitMapper rechargeLimitMapper;

    @Override
    public Optional<RechargeLimit> findById(RechargeLimitKey id) {
        if (id == null) {
            return Optional.empty();
        }

        return rechargeLimitJpaRepository.findById(toEntityKey(id))
                .map(rechargeLimitMapper::toDomain);
    }


    @Override
    public List<RechargeLimit> findAll() {
        return rechargeLimitJpaRepository.findAllRechargeLimits().stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
    }



    @Override
    public boolean existsById(RechargeLimitKey id) {
        if (id == null) {
            return false;
        }
        return rechargeLimitJpaRepository.existsById(toEntityKey(id));
    }

    @Override
    public RechargeLimit save(RechargeLimit entity) {
        var saved = rechargeLimitJpaRepository.save(rechargeLimitMapper.toEntity(entity));
        return rechargeLimitMapper.toDomain(saved);
    }



    private RechargeLimitKeyEntityJpa toEntityKey(RechargeLimitKey id) {
        return new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
    }
}
