package br.sptrans.scd.creditrequest.application.service;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.domain.CreditRequest;

@Mapper(componentModel = "spring")
public interface CreditRequestMapper {

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
}
