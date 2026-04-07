package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.CreditRequestItemJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestItensAdapter implements CreditRequestItemsPort {

    private final CreditRequestItemJpaRepository itemJpaRepository;
    private final CreditRequestMapper creditRequestMapper;

    @Override
    public CreditRequestItems save(CreditRequestItems items) {
        CreditRequestItemsEJpa saved = itemJpaRepository.save(creditRequestMapper.toEntityItem(items));
        return creditRequestMapper.toDomainItem(saved);
    }

    @Override
    public List<CreditRequestItemsEJpa> findFirstBySituacaoAndDtPagtoEconomicaBetween(String codSituacao, Timestamp dtInicio, Timestamp dtFim, Integer limit) {
        return itemJpaRepository.findFirstBySituacaoAndDtPagtoEconomicaBetween(codSituacao, dtInicio, dtFim, limit);
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

    @Override
    public List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(Long numSolicitacao, String codCanal, String numLote) {
        return itemJpaRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
    }

    @Override
    public List<CreditRequestItemsEJpa> findProcessRechargeService(Long numSolicitacao, String codCanal,
            String numLote) {
        return itemJpaRepository.findProcessRechargeService(numSolicitacao, codCanal, numLote);
    }

}
