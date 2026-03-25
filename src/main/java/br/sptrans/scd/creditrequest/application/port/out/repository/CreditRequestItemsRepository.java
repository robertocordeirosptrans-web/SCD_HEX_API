package br.sptrans.scd.creditrequest.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;

public interface CreditRequestItemsRepository {

    CreditRequestItems save(CreditRequestItemsEJpa items);

    List<CreditRequestItemsEJpa> findById_NumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal);

    Optional<CreditRequestItemsEJpa> findById(CreditRequestItemsEJpaKey id);
}
