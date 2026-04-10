package br.sptrans.scd.creditrequest.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestRDEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestRDEntityKey;

public interface CreditRequestRDJpaRepository
        extends JpaRepository<CreditRequestRDEntity, CreditRequestRDEntityKey> {

    List<CreditRequestRDEntity> findByIdNumSolicitacaoAndIdCodCanal(Long numSolicitacao, String codCanal);
}
