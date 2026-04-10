package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEntityKey;
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
        CreditRequestItemsEntity saved = itemJpaRepository.save(creditRequestMapper.toEntityItem(items));
        return creditRequestMapper.toDomainItem(saved);
    }

    @Override
    public List<CreditRequestItems> searchForItensUnlocked(String codSituacao) {
        return itemJpaRepository.searchForItensUnlocked(codSituacao)
                .stream()
                .map(creditRequestMapper::toDomainItem)
                .toList();
    }

    @Override
    public Optional<CreditRequestItems> findById(CreditRequestItemsKey id) {
        if (id == null) {
            return Optional.empty();
        }
        CreditRequestItemsEntityKey entityKey = new CreditRequestItemsEntityKey(id.getNumSolicitacao(), id.getNumSolicitacaoItem(), id.getCodCanal());
        return itemJpaRepository.findById(entityKey)
                .map(creditRequestMapper::toDomainItem);
    }

    @Override
    public List<Long> findNumSolicitacaoItemsBySolicitacaoCanalLote(Long numSolicitacao, String codCanal, String numLote) {
        return itemJpaRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
    }

    @Override
    public List<CreditRequestItems> findProcessRechargeService(Long numSolicitacao, String codCanal, String numLote) {
        return itemJpaRepository.findProcessRechargeService(numSolicitacao, codCanal, numLote)
                .stream()
                .map(creditRequestMapper::toDomainItem)
                .toList();
    }

    @Override
    public List<CreditRequestItems> findAllBySolicitacao(Long numSolicitacao, String codCanal) {
        return itemJpaRepository.findAllBySolicitacao(numSolicitacao, codCanal).stream()
                .map(creditRequestMapper::toDomainItem)
                .toList();
    }

    @Override
    public List<CreditRequestItems> searchItemsToBeProcessed(String codSituacao) {
        return itemJpaRepository.searchItemsToBeProcessed(codSituacao)
                .stream()
                .map(creditRequestMapper::toDomainItem)
                .toList();
    }

    @Override
    public List<CreditRequestItems> findAllById(List<CreditRequestItemsKey> ids) {
        List<CreditRequestItemsEntityKey> entityKeys = ids.stream()
                .map(id -> new CreditRequestItemsEntityKey(
                        id.getNumSolicitacao(),
                        id.getNumSolicitacaoItem(),
                        id.getCodCanal()))
                .toList();
        return itemJpaRepository.findAllById(entityKeys).stream()
                .map(creditRequestMapper::toDomainItem)
                .toList();
    }

    @Override
    public List<CreditRequestItems> searchItemsToBeConfirmed(String codSituacao, Integer limit) {
        return itemJpaRepository.searchItemsToBeConfirmed(codSituacao, limit)
                .stream()
                .map(creditRequestMapper::toDomainItem)
                .toList();
    }

}
