package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.RechargeLimitKeyEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.RechargeLimitMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.RechargeLimitJpaRepository;
import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RechargeLimitAdapterJpa implements RechargeLimitRepository {

    private final RechargeLimitJpaRepository repository;
    private final RechargeLimitMapper mapper;

    @Override
    public Optional<RechargeLimit> findById(RechargeLimitKey id) {
        RechargeLimitKeyEntityJpa keyEntity = new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return repository.findById(keyEntity).map(mapper::toDomain);
    }

    @Override
    public List<RechargeLimit> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RechargeLimit> findByCodCanal(String codCanal) {
        return repository.findByCodCanal(codCanal).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RechargeLimit> findByCodProduto(String codProduto) {
        return repository.findByCodProduto(codProduto).stream().map(mapper::toDomain).toList();
    }

    @Override
    public RechargeLimit save(RechargeLimit entity) {
        var saved = repository.save(mapper.toEntity(entity));
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(RechargeLimitKey id) {
        RechargeLimitKeyEntityJpa keyEntity = new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        repository.deleteById(keyEntity);
    }

    @Override
    public boolean existsById(RechargeLimitKey id) {
        RechargeLimitKeyEntityJpa keyEntity = new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return repository.existsById(keyEntity);
    }

    @Override
    public Optional<RechargeLimit> findByIdOtimized(String codCanal, String codProduto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
