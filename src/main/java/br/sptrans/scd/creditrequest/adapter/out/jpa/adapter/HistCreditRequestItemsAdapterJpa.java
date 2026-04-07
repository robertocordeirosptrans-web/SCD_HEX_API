package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

// ...existing code...
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.HistCreditItensMapper;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.HistCreditItemJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.HistCreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItemsKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistCreditRequestItemsAdapterJpa implements HistCreditRequestItemsRepository {


    private final HistCreditItemJpaRepository repository;



    @Override
    public HistCreditRequestItems save(HistCreditRequestItems h) {
        var entity = HistCreditItensMapper.toEntity(h);
        var saved = repository.save(entity);
        return HistCreditItensMapper.toDomain(saved);
    }

    @Override
    public List<HistCreditRequestItems> saveAll(List<HistCreditRequestItems> items) {
        var entities = items.stream().map(HistCreditItensMapper::toEntity).toList();
        var saved = repository.saveAll(entities);
        return saved.stream().map(HistCreditItensMapper::toDomain).toList();
    }

    @Override
        public Optional<HistCreditRequestItems> findById(HistCreditRequestItemsKey id) {
            return repository.findByIdAllFields(
                    id.getNumSolicitacao(),
                    id.getNumSolicitacaoItem(),
                    id.getCodCanal(),
                    id.getSeqHistSdis()
            ).map(HistCreditItensMapper::toDomain);
        }

    @Override
    public List<HistCreditRequestItems> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        return repository.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .stream().map(HistCreditItensMapper::toDomain).toList();
    }

    @Override
    public List<HistCreditRequestItems> findAll() {
        return repository.findAllOrdered().stream().map(HistCreditItensMapper::toDomain).toList();
    }

    @Override
        public boolean existsById(HistCreditRequestItemsKey id) {
            return repository.countById(
                    id.getNumSolicitacao(),
                    id.getNumSolicitacaoItem(),
                    id.getCodCanal(),
                    id.getSeqHistSdis()
            ) > 0;
        }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Long findMaxSeqHistSdis(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        return repository.findMaxSeqHistSdis(numSolicitacao, numSolicitacaoItem, codCanal);
    }

    @Override
    public List<HistCreditRequestItems> findLatestByItem(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        return repository.findLatestByItem(numSolicitacao, numSolicitacaoItem, codCanal)
                .stream().map(HistCreditItensMapper::toDomain).toList();
    }

   
}
