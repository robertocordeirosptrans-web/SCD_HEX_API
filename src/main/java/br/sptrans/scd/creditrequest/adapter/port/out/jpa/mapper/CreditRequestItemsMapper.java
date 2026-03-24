package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import org.mapstruct.Mapper;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestItemsDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;

@Mapper(componentModel = "spring")
public interface CreditRequestItemsMapper {
    CreditRequestItemsDTO toDTO(CreditRequestItems item);
}
