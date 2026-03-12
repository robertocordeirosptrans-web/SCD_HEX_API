package br.sptrans.scd.creditrequest.application.service;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestItemsDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;


@Mapper(componentModel = "spring")
public interface CreditRequestMapper {

    @Mapping(target = "itens", source = "itens", qualifiedByName = "mapItens")
    CreditRequestDTO toDTO(CreditRequest entity);

    List<CreditRequestDTO> toDTOList(List<CreditRequest> entities);

    @Mapping(target = "dtEnvioHm", ignore = true)
    @Mapping(target = "sqPid", ignore = true)
    @Mapping(target = "dtInicProcesso", ignore = true)
    @Mapping(target = "vlServicoRecarga", ignore = true)
    @Mapping(target = "vlServicoAdm", ignore = true)
    @Mapping(target = "flgEvento", ignore = true)
    @Mapping(target = "vlEvento", ignore = true)
    @Mapping(target = "itens", ignore = true)
    CreditRequest toEntity(CreditRequestDTO dto);

    @Named("mapItens")
    default List<CreditRequestItemsDTO> mapItens(List<CreditRequestItems> itens) {
        if (itens == null) {
            return List.of();
        }
        return itens.stream()
            .map(this::itemToDTO)
            .toList();
    }

    @Mapping(target = "numSolicitacaoItem", source = "id.numSolicitacaoItem")
    @Mapping(target = "vlTxadm", source = "vlTxadm")
    @Mapping(target = "vlTxserv", source = "vlTxserv")
    @Mapping(target = "vlTxtotal", source = "vlTxtotal")
    CreditRequestItemsDTO itemToDTO(CreditRequestItems entity);
}
