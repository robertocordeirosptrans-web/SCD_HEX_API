package br.sptrans.scd.auth.adapter.port.out.jpa.mapper;

import br.sptrans.scd.auth.adapter.port.out.persistence.entity.ClassificationPersonEntity;
import br.sptrans.scd.auth.domain.ClassificationPerson;

public class ClassificationPersonMapper {

    public static ClassificationPerson toDomain(ClassificationPersonEntity entity) {
        if (entity == null) return null;
        return new ClassificationPerson(
                entity.getCodClassificacaoPessoa(),
                entity.getDesClassificacaoPessoa(),
                entity.getFlgVenda(),
                entity.getDtCadastro(),
                entity.getDtManutencao(),
                entity.getStClassificacoesPessoa(),
                null,
                null);
    }
}
