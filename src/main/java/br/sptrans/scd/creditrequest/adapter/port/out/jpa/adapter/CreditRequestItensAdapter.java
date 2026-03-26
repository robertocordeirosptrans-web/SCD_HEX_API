package br.sptrans.scd.creditrequest.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.repository.CreditRequestItemJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestItensAdapter implements CreditRequestItemsRepository {

    private final CreditRequestItemJpaRepository itemJpaRepository;
    private final CreditRequestMapper creditRequestMapper;

    @Override
    public CreditRequestItems save(CreditRequestItems items) {
        CreditRequestItemsEJpa saved = itemJpaRepository.save(creditRequestMapper.toEntityItem(items));
        return creditRequestMapper.toDomainItem(saved);
    }

    @Override
    public List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(Long numSolicitacao, String codCanal, String numLote) {
        return itemJpaRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
    }

    @Override
    public Optional<CreditRequestItems> findById(CreditRequestItemsKey id) {
        if (id == null) {
            return Optional.empty();
        }
        CreditRequestItemsEJpaKey entityKey = new CreditRequestItemsEJpaKey(id.getNumSolicitacao(), id.getNumSolicitacaoItem(), id.getCodCanal());
        return itemJpaRepository.findById(entityKey)
                .map(creditRequestMapper::toDomainItem);
    }

}
