package br.sptrans.scd.auth.adapter.port.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.port.out.jpa.entity.GroupEntityJpa;
import br.sptrans.scd.auth.domain.Group;

public class GroupMapper {
    public static Group toDomain(GroupEntityJpa entity) {
        if (entity == null) return null;
        Group group = new Group();
        group.setCodGrupo(entity.getCodGrupo());
        group.setNomGrupo(entity.getNomGrupo());
        group.setCodStatus(entity.getCodStatus());
        group.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        // Conversão de datas e coleções pode ser expandida conforme necessário
        return group;
    }

    public static GroupEntityJpa toEntity(Group domain) {
        if (domain == null) return null;
        GroupEntityJpa entity = new GroupEntityJpa();
        entity.setCodGrupo(domain.getCodGrupo());
        entity.setNomGrupo(domain.getNomGrupo());
        entity.setCodStatus(domain.getCodStatus());
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        // Conversão de datas e coleções pode ser expandida conforme necessário
        return entity;
    }
}
