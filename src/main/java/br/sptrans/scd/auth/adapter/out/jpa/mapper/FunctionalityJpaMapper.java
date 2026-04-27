
package br.sptrans.scd.auth.adapter.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.FunctionalityEntityJpaKey;
import br.sptrans.scd.auth.domain.Functionality;
import br.sptrans.scd.auth.domain.FunctionalityKey;

public class FunctionalityJpaMapper {
    public static FunctionalityEntityJpa toEntity(Functionality domain) {
        if (domain == null) return null;
        FunctionalityEntityJpa entity = new FunctionalityEntityJpa();
        entity.setId(toEntityKey(domain.getId()));
        entity.setNomFuncionalidade(domain.getNomFuncionalidade());
        entity.setCodStatus(domain.getCodStatus());
        entity.setDtManutencao(domain.getDtModi());
        entity.setDtCadastro(domain.getDtSinc());
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        entity.setFlgMonitoracao(domain.getFlgMonitoracao());
        entity.setFlgEvento(domain.getFlgEvento());
        return entity;
    }

    public static Functionality toDomain(FunctionalityEntityJpa entity) {
        if (entity == null) return null;
        Functionality domain = new Functionality();
        domain.setId(toDomainKey(entity.getId()));
        domain.setNomFuncionalidade(entity.getNomFuncionalidade());
        domain.setCodStatus(entity.getCodStatus());
        domain.setDtModi(entity.getDtManutencao());
        domain.setDtSinc(entity.getDtCadastro());
        domain.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        domain.setFlgMonitoracao(entity.getFlgMonitoracao());
        domain.setFlgEvento(entity.getFlgEvento());
        // domain.setId(id);
        // if (entity.getId() != null) {
        //     domain.setCodSistema(entity.getId().getCodSistema());
        //     domain.setCodModulo(entity.getId().getCodModulo());
        //     domain.setCodRotina(entity.getId().getCodRotina());
        //     domain.setCodFuncionalidade(entity.getId().getCodFuncionalidade());
        // }
        return domain;
    }

    public static FunctionalityEntityJpaKey toEntityKey(FunctionalityKey key) {
        if (key == null) return null;
        return new FunctionalityEntityJpaKey(
            key.getCodSistema(),
            key.getCodModulo(),
            key.getCodRotina(),
            key.getCodFuncionalidade()
        );
    }

    public static FunctionalityKey toDomainKey(FunctionalityEntityJpaKey key) {
        if (key == null) return null;
        return new FunctionalityKey(
            key.getCodSistema(),
            key.getCodModulo(),
            key.getCodRotina(),
            key.getCodFuncionalidade()
        );
    }
}
