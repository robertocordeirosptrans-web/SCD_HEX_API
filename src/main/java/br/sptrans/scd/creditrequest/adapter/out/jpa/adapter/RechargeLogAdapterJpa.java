package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.RechargeLogEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.RechargeLogMapper;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.RechargeLogJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.RechargeLogPort;
import br.sptrans.scd.creditrequest.domain.RechargeLog;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class  RechargeLogAdapterJpa implements  RechargeLogPort {
    private final RechargeLogJpaRepository rechargeLogJpaRepository;

    @Override

    public RechargeLog save(RechargeLog rechargeLog) {
        RechargeLogEJpa entity = RechargeLogMapper.toEntity(rechargeLog);
        RechargeLogEJpa saved = rechargeLogJpaRepository.save(entity);
        return RechargeLogMapper.toDomain(saved);
    }

    @Override

    public Optional<RechargeLog> findById(Integer seqRecarga) {
        return rechargeLogJpaRepository.findById(seqRecarga)
                .map(RechargeLogMapper::toDomain);
    }

    @Override

    public Optional<RechargeLog> findByNumLogicoCartao(String numLogicoCartao) {
        return rechargeLogJpaRepository.findByNumLogicoCartao(numLogicoCartao)
                .map(RechargeLogMapper::toDomain);
    }

    @Override
    public Optional<Integer> findMaxSeqRecarga() {
        int lastSeq = rechargeLogJpaRepository.nextSeqRecarga() - 1;
        return lastSeq > 0 ? Optional.of(lastSeq) : Optional.empty();
    }


    
}
