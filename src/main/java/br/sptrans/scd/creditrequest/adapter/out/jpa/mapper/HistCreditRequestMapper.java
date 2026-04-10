package br.sptrans.scd.creditrequest.adapter.out.jpa.mapper;


import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEntityKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;

public interface HistCreditRequestMapper {
    HistCreditRequest toDomain(HistCreditRequestEntity entity);
    HistCreditRequestEntity toEntity(HistCreditRequest domain);
    HistCreditRequestEntityKey toEntityKey(HistCreditRequestKey key);
    HistCreditRequestKey toDomainKey(HistCreditRequestEntityKey entityKey);
}
