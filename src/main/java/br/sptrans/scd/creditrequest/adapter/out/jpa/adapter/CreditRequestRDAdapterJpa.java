package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestRDEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestRDMapper;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.CreditRequestRDJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRDPort;
import br.sptrans.scd.creditrequest.domain.CreditRequestRD;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestRDAdapterJpa implements CreditRequestRDPort {

    private final CreditRequestRDJpaRepository jpaRepository;
    private final CreditRequestRDMapper mapper;

    @Override
    public void saveAll(List<CreditRequestRD> distribuicoes) {
        List<CreditRequestRDEntity> entities =
                distribuicoes.stream().map(mapper::toEntity).toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<CreditRequestRD> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        return jpaRepository.findByIdNumSolicitacaoAndIdCodCanal(numSolicitacao, codCanal)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
