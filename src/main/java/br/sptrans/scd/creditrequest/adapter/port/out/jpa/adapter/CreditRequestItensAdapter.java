package br.sptrans.scd.creditrequest.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestItensAdapter implements CreditRequestItemsRepository{
    
    @Override
    public CreditRequestItems save(CreditRequestItemsEJpa items) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public List<CreditRequestItemsEJpa> findById_NumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById_NumSolicitacaoAndCodCanal'");
    }

    @Override
    public Optional<CreditRequestItemsEJpa> findById(CreditRequestItemsEJpaKey id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
    
}
