package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.out.jpa.entity.ModalityEntityJpa;
import br.sptrans.scd.product.domain.Modality;

public interface ModalityMapper {

    static Modality toDomain(ModalityEntityJpa entity) {
        if (entity == null) {
            return null;
        }
        Modality modality = new Modality();
        modality.setCodModalidade(entity.getCodModalidade());
        modality.setDesModalidade(entity.getDesModalidade());
        modality.setDtCadastro(entity.getDtCadastro());
        modality.setDtManutencao(entity.getDtManutencao());
        modality.setCodStatus(entity.getCodStatus());

        if (entity.getIdUsuarioCadastro() != null) {
            User user = new User();
            user.setIdUsuario(entity.getIdUsuarioCadastro().getIdUsuario());
            modality.setIdUsuarioCadastro(user);
        }
        if (entity.getIdUsuarioManutencao() != null) {
            User user = new User();
            user.setIdUsuario(entity.getIdUsuarioManutencao().getIdUsuario());
            modality.setIdUsuarioManutencao(user);
        }

        return modality;
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

        if (modality.getIdUsuarioCadastro() != null) {
            User user = new User();
            user.setIdUsuario(modality.getIdUsuarioCadastro().getIdUsuario());
            entity.setIdUsuarioCadastro(user);
        }
        if (modality.getIdUsuarioManutencao() != null) {
            User user = new User();
            user.setIdUsuario(modality.getIdUsuarioManutencao().getIdUsuario());
            entity.setIdUsuarioManutencao(user);
        }

        return entity;
    }

}
