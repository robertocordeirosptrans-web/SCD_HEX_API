package br.sptrans.scd.creditrequest.adapter.out.jpa.mapper;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.RechargeLogEntity;
import br.sptrans.scd.creditrequest.domain.RechargeLog;

public class RechargeLogMapper {
    public static RechargeLogEntity toEntity(RechargeLog domain) {
        if (domain == null) return null;
        RechargeLogEntity entity = new RechargeLogEntity();
        entity.setSeqRecarga(domain.getSeqRecarga());
        entity.setNumLogicoCartao(domain.getNumLogicoCartao());
        entity.setDtSolicRecarga(domain.getDtSolicRecarga());
        entity.setDtCadastro(domain.getDtCadastro());
        entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro());
        entity.setDtManutencao(domain.getDtManutencao());
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        return entity;
    }

    public static RechargeLog toDomain(RechargeLogEntity entity) {
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
