package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.RechargeLogEJpa;
import br.sptrans.scd.creditrequest.domain.RechargeLog;

public class RechargeLogMapper {
    public static RechargeLogEJpa toEntity(RechargeLog domain) {
        if (domain == null) return null;
        RechargeLogEJpa entity = new RechargeLogEJpa();
        entity.setSeqRecarga(domain.getSeqRecarga());
        entity.setNumLogicoCartao(domain.getNumLogicoCartao());
        entity.setDtSolicRecarga(domain.getDtSolicRecarga());
        entity.setDtCadastro(domain.getDtCadastro());
        entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro());
        entity.setDtManutencao(domain.getDtManutencao());
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        return entity;
    }

    public static RechargeLog toDomain(RechargeLogEJpa entity) {
        if (entity == null) return null;
        RechargeLog domain = new RechargeLog();
        domain.setSeqRecarga(entity.getSeqRecarga());
        domain.setNumLogicoCartao(entity.getNumLogicoCartao());
        domain.setDtSolicRecarga(entity.getDtSolicRecarga());
        domain.setDtCadastro(entity.getDtCadastro());
        domain.setIdUsuarioCadastro(entity.getIdUsuarioCadastro());
        domain.setDtManutencao(entity.getDtManutencao());
        domain.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        return domain;
    }
}
