package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.ServiceFeeEntityJpa;
import br.sptrans.scd.product.domain.ServiceFee;

public interface ServiceFeeMapper {
    static ServiceFee toDomain(ServiceFeeEntityJpa entity) {
        if (entity == null) return null;
        return new ServiceFee(
            entity.getCodTaxaSrv(),
            entity.getRecInicial(),
            entity.getRecFinal(),
            entity.getValFixo(),
            entity.getValPercentual(),
            entity.getValMinimo(),
            null // taxa (Fee) - relacionamento não mapeado
        );
    }

    static ServiceFeeEntityJpa toEntity(ServiceFee fee) {
        if (fee == null) return null;
        ServiceFeeEntityJpa entity = new ServiceFeeEntityJpa();
        entity.setCodTaxaSrv(fee.getCodTaxaSrv());
        entity.setRecInicial(fee.getRecInicial());
        entity.setRecFinal(fee.getRecFinal());
        entity.setValFixo(fee.getValFixo());
        entity.setValPercentual(fee.getValPercentual());
        entity.setValMinimo(fee.getValMinimo());
        return entity;
    }
}
