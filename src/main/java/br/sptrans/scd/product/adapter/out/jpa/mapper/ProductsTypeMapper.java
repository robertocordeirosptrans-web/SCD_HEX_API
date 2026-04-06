package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.out.persistence.entity.ModalityEntityJpa;
import br.sptrans.scd.product.adapter.out.persistence.entity.ProductTypesEntityJpa;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.ProductType;

public interface ProductsTypeMapper {
      static ProductType toDomain(ProductTypesEntityJpa entity, UserPersistencePort userRepository) {
        if (entity == null) {
            return null;
        }
        
        ProductType type = new ProductType();
        type.setCodTipoProduto(entity.getCodTipoProduto());
        type.setDesTipoProduto(entity.getDesTipoProduto());
        type.setDtCadastro(entity.getDtCadastro());
        type.setDtManutencao(entity.getDtManutencao());
        type.setCodStatus(entity.getCodStatus());
        if (entity.getIdUsuarioCadastro() != null) {
            User user = userRepository.findById(entity.getIdUsuarioCadastro()).orElse(null);
            type.setIdUsuarioCadastro(user);
        }
        if (entity.getIdUsuarioManutencao() != null) {
            User user = userRepository.findById(entity.getIdUsuarioManutencao()).orElse(null);
            type.setIdUsuarioManutencao(user);
        }
        return type;
    }

    static ModalityEntityJpa toEntity(Modality modality) {
        if (modality == null) {
            return null;
        }
        ModalityEntityJpa entity = new ModalityEntityJpa();
        entity.setCodModalidade(modality.getCodModalidade());
        entity.setDesModalidade(modality.getDesModalidade());
        entity.setDtCadastro(modality.getDtCadastro());
        entity.setDtManutencao(modality.getDtManutencao());
        entity.setCodStatus(modality.getCodStatus());


        return entity;
    }
}
