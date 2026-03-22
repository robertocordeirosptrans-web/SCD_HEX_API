package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.RechargeLimitMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.RechargeLimitJpaRepository;

import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;

import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RechargeLimitAdapterJpa implements RechargeLimitRepository {

    private final RechargeLimitJpaRepository rechargeLimitJpaRepository;
    private final RechargeLimitMapper rechargeLimitMapper;

    @Override
    public Optional<RechargeLimit> findById(RechargeLimitKey id) {
        return rechargeLimitJpaRepository.findById(rechargeLimitMapper.toEntityKey(id))
                .map(rechargeLimitMapper::toDomain);
    }

    @Override
    public List<RechargeLimit> findAll() {
        return rechargeLimitJpaRepository.findAll().stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
    }

    @Override
    public List<RechargeLimit> findByCodCanal(String codCanal) {
        return rechargeLimitJpaRepository.findByCodCanal(codCanal).stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
    }

    @Override
    public List<RechargeLimit> findByCodProduto(String codProduto) {
        return rechargeLimitJpaRepository.findByCodProduto(codProduto).stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
    }

    @Override
    public RechargeLimit save(RechargeLimit entity) {
        var saved = rechargeLimitJpaRepository.save(rechargeLimitMapper.toEntity(entity));
        return rechargeLimitMapper.toDomain(saved);
    }

    @Override
    public void deleteById(RechargeLimitKey id) {
        rechargeLimitJpaRepository.deleteById(rechargeLimitMapper.toEntityKey(id));
    }

    @Override
    public boolean existsById(RechargeLimitKey id) {
        return rechargeLimitJpaRepository.existsById(rechargeLimitMapper.toEntityKey(id));
    }
}
