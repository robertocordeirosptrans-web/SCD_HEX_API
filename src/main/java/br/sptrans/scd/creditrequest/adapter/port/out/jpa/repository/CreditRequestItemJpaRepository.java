package br.sptrans.scd.creditrequest.adapter.port.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;

public interface CreditRequestItemJpaRepository extends JpaRepository<CreditRequestItemsEJpa, CreditRequestItemsEJpaKey>, JpaSpecificationExecutor<CreditRequestItemsEJpa>{
    
}
