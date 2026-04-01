package br.sptrans.scd.product.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.application.port.out.UserRepository;
import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.port.out.persistence.entity.FamilyEntityJpa;
import br.sptrans.scd.product.domain.Family;

public interface FamilyMapper {

    static Family toDomain(FamilyEntityJpa entity, UserRepository userRepository) {
        if (entity == null) {
            return null;
        }
        User usuarioCadastro = entity.getIdUsuarioCadastro() != null
                ? userRepository.findById(entity.getIdUsuarioCadastro()).orElse(null)
                : null;
        User usuarioManutencao = entity.getIdUsuarioManutencao() != null
                ? userRepository.findById(entity.getIdUsuarioManutencao()).orElse(null)
                : null;
        return new Family(
                entity.getCodFamilia(),
                entity.getDesFamilia(),
                entity.getStFamilias(),
                entity.getDtCadastro(),
                entity.getDtManutencao(),
                usuarioCadastro,
                usuarioManutencao
        );
    }

    static FamilyEntityJpa toEntity(Family family) {
        if (family == null) {
            return null;
        }
        FamilyEntityJpa entity = new FamilyEntityJpa();
        entity.setCodFamilia(family.getCodFamilia());
        entity.setDesFamilia(family.getDesFamilia());
        entity.setStFamilias(family.getStFamilias());
        entity.setDtCadastro(family.getDtCadastro());
        entity.setDtManutencao(family.getDtManutencao());
        // idUsuarioCadastro, idUsuarioManutencao: implementar se necessário
        return entity;
    }
}
