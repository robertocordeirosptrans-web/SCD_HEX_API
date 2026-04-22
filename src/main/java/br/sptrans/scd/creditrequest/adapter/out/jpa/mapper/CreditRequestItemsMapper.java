package br.sptrans.scd.creditrequest.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestItemsDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;

@Mapper(componentModel = "spring")
public interface CreditRequestItemsMapper {

    @Mapping(target = "numSolicitacaoItem", ignore = true)
    CreditRequestItemsDTO toDTO(CreditRequestItems item);
}
