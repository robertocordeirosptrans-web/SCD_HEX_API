package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.out.jpa.entity.ModalityEntityJpa;
import br.sptrans.scd.product.adapter.out.jpa.entity.TechnologyEntityJpa;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.Technology;

public interface TechnologyMapper {
    static Technology toDomain(TechnologyEntityJpa entity) {
        if (entity == null) {
            return null;
        }
        Technology tech = new Technology();
        tech.setCodTecnologia(entity.getCodTecnologia());
        tech.setDesTecnologia(entity.getDesTecnologia());
        tech.setDtCadastro(entity.getDtCadastro());
        tech.setDtManutencao(entity.getDtManutencao());
        tech.setCodStatus(entity.getCodStatus());

        if (entity.getIdUsuarioCadastro() != null) {
            User user = new User();
            user.setIdUsuario(entity.getIdUsuarioCadastro().getIdUsuario());
            tech.setIdUsuarioCadastro(user);
        }
        if (entity.getIdUsuarioManutencao() != null) {
            User user = new User();
            user.setIdUsuario(entity.getIdUsuarioManutencao().getIdUsuario());
            tech.setIdUsuarioManutencao(user);
        }

        return tech;
    }

    static TechnologyEntityJpa toEntity(Technology tech) {
        if (tech == null) {
            return null;
        }
        TechnologyEntityJpa entity = new TechnologyEntityJpa();
        entity.setCodTecnologia(tech.getCodTecnologia());
        entity.setDesTecnologia(tech.getDesTecnologia());
        entity.setDtCadastro(tech.getDtCadastro());
        entity.setDtManutencao(tech.getDtManutencao());
        entity.setCodStatus(tech.getCodStatus());

        if (tech.getIdUsuarioCadastro() != null) {
            User user = new User();
            user.setIdUsuario(tech.getIdUsuarioCadastro().getIdUsuario());
            entity.setIdUsuarioCadastro(user);
        }
        if (tech.getIdUsuarioManutencao() != null) {
            User user = new User();
            user.setIdUsuario(tech.getIdUsuarioManutencao().getIdUsuario());
            entity.setIdUsuarioManutencao(user);
        }

        return entity;
    }
}
