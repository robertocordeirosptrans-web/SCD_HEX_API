package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.product.adapter.port.out.persistence.entity.TechnologyEntityJpa;
import br.sptrans.scd.product.domain.Technology;

public interface TechnologyMapper {
    static Technology toDomain(TechnologyEntityJpa entity, UserRepository userRepository) {
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
            User user = userRepository.findById(entity.getIdUsuarioCadastro()).orElse(null);
            tech.setIdUsuarioCadastro(user);
        }
        if (entity.getIdUsuarioManutencao() != null) {
            User user = userRepository.findById(entity.getIdUsuarioManutencao()).orElse(null);
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
            entity.setIdUsuarioCadastro(tech.getIdUsuarioCadastro().getIdUsuario());
        }
        if (tech.getIdUsuarioManutencao() != null) {
            entity.setIdUsuarioManutencao(tech.getIdUsuarioManutencao().getIdUsuario());
        }

        return entity;
    }
}
