package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

<<<<<<< HEAD
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.RechargeLimitMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.RechargeLimitJpaRepository;

import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;

=======
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.RechargeLimitMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.RechargeLimitJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.RechargeLimitKeyEntityJpa;
import br.sptrans.scd.channel.application.port.out.RechargeLimitRepository;
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RechargeLimitAdapterJpa implements RechargeLimitRepository {

<<<<<<< HEAD
    private final RechargeLimitJpaRepository rechargeLimitJpaRepository;
    private final RechargeLimitMapper rechargeLimitMapper;

    @Override
    public Optional<RechargeLimit> findById(RechargeLimitKey id) {
        return rechargeLimitJpaRepository.findById(rechargeLimitMapper.toEntityKey(id))
                .map(rechargeLimitMapper::toDomain);
=======
    private final RechargeLimitJpaRepository repository;
    private final RechargeLimitMapper mapper;

    @Override
    public Optional<RechargeLimit> findById(RechargeLimitKey id) {
        RechargeLimitKeyEntityJpa keyEntity = new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return repository.findById(keyEntity).map(mapper::toDomain);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public List<RechargeLimit> findAll() {
<<<<<<< HEAD
        return rechargeLimitJpaRepository.findAll().stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
=======
        return repository.findAll().stream().map(mapper::toDomain).toList();
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public List<RechargeLimit> findByCodCanal(String codCanal) {
<<<<<<< HEAD
        return rechargeLimitJpaRepository.findByCodCanal(codCanal).stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
=======
        return repository.findByCodCanal(codCanal).stream().map(mapper::toDomain).toList();
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public List<RechargeLimit> findByCodProduto(String codProduto) {
<<<<<<< HEAD
        return rechargeLimitJpaRepository.findByCodProduto(codProduto).stream()
                .map(rechargeLimitMapper::toDomain)
                .toList();
=======
        return repository.findByCodProduto(codProduto).stream().map(mapper::toDomain).toList();
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public RechargeLimit save(RechargeLimit entity) {
<<<<<<< HEAD
        var saved = rechargeLimitJpaRepository.save(rechargeLimitMapper.toEntity(entity));
        return rechargeLimitMapper.toDomain(saved);
=======
        var saved = repository.save(mapper.toEntity(entity));
        return mapper.toDomain(saved);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public void deleteById(RechargeLimitKey id) {
<<<<<<< HEAD
        rechargeLimitJpaRepository.deleteById(rechargeLimitMapper.toEntityKey(id));
=======
        RechargeLimitKeyEntityJpa keyEntity = new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        repository.deleteById(keyEntity);
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }

    @Override
    public boolean existsById(RechargeLimitKey id) {
<<<<<<< HEAD
        return rechargeLimitJpaRepository.existsById(rechargeLimitMapper.toEntityKey(id));
=======
        RechargeLimitKeyEntityJpa keyEntity = new RechargeLimitKeyEntityJpa(id.getCodCanal(), id.getCodProduto());
        return repository.existsById(keyEntity);
    }

    @Override
    public Optional<RechargeLimit> findByIdOtimized(String codCanal, String codProduto) {
        throw new UnsupportedOperationException("Not supported yet.");
>>>>>>> 46368b76967eb4ccf485dc787a2cfe1535317aa2
    }
}
