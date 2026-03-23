package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.domain.DestinyFee;
import br.sptrans.scd.product.adapter.out.jpa.entity.DestinyFeeEntityJpa;

public interface DestinyFeeMapper {
    static DestinyFee toDomain(DestinyFeeEntityJpa entity) {
        if (entity == null) return null;
        return new DestinyFee(
            entity.getCodTaxaDes(),
            entity.getCodCanalDestino()
        );
    }

    static DestinyFeeEntityJpa toEntity(DestinyFee fee) {
        if (fee == null) return null;
        DestinyFeeEntityJpa entity = new DestinyFeeEntityJpa();
        entity.setCodTaxaDes(fee.getCodTaxaDes());
        entity.setCodCanalDestino(fee.getCodCanalDestino());
        return entity;
    }
}
