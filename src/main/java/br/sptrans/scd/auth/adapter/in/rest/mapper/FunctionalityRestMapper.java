package br.sptrans.scd.auth.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.sptrans.scd.auth.adapter.in.rest.dto.FunctionalityResponseDTO;
import br.sptrans.scd.auth.domain.Functionality;

@Mapper(componentModel = "spring")
public interface FunctionalityRestMapper {
    FunctionalityRestMapper INSTANCE = Mappers.getMapper(FunctionalityRestMapper.class);

    FunctionalityResponseDTO toDto(Functionality entity);
}
