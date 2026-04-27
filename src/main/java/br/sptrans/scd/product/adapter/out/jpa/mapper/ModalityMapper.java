package br.sptrans.scd.product.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.sptrans.scd.product.adapter.out.persistence.entity.ModalityEntityJpa;
import br.sptrans.scd.product.domain.Modality;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserEntityJpaMapper.class)
public interface ModalityMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(source = "usuarioCadastro", target = "idUsuarioCadastro")
    @Mapping(source = "usuarioManutencao", target = "idUsuarioManutencao")
    Modality toDomain(ModalityEntityJpa entity);

    @Mapping(source = "idUsuarioCadastro", target = "usuarioCadastro")
    @Mapping(source = "idUsuarioManutencao", target = "usuarioManutencao")
    ModalityEntityJpa toEntity(Modality modality);
}
