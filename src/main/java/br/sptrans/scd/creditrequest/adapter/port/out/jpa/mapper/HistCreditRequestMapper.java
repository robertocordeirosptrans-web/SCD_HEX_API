package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestKeyEJpa;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;

public interface HistCreditRequestMapper {
    HistCreditRequest toDomain(HistCreditRequestEJpa entity);
    HistCreditRequestEJpa toEntity(HistCreditRequest domain);
    HistCreditRequestKeyEJpa toEntityKey(HistCreditRequestKey key);
    HistCreditRequestKey toDomainKey(HistCreditRequestKeyEJpa entityKey);
}
