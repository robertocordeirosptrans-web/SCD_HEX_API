package br.sptrans.scd.creditrequest.application.port.out.mapper;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequest;

public interface CreditRequestDTOMapper {
    CreditRequestDTO toDTO(CreditRequest creditRequest);
}
