package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.product.adapter.out.jpa.entity.ModalityEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.ProductTypesEntityJpa;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.ProductType;

public interface ProductsTypeMapper {
      static ProductType toDomain(ProductTypesEntityJpa entity) {
        if (entity == null) {
            return null;
        }
        
        ProductType type = new ProductType();
        type.setCodTipoProduto(entity.getCodTipoProduto());
        type.setDesTipoProduto(entity.getDesTipoProduto());
        type.setDtCadastro(entity.getDtCadastro());
        type.setDtManutencao(entity.getDtManutencao());
        type.setCodStatus(entity.getCodStatus());


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
