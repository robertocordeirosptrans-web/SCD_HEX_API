package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.entity.AgreementValidityKeyEntityJpa;
import br.sptrans.scd.channel.domain.AgreementValidity;
import br.sptrans.scd.channel.domain.AgreementValidityKey;
import org.springframework.lang.NonNull;

public class AgreementValidityMapper {
    public static @NonNull AgreementValidityEntityJpa toEntity(AgreementValidity domain) {
        if (domain == null) return null;
        AgreementValidityEntityJpa entity = new AgreementValidityEntityJpa();
        entity.setId(new AgreementValidityKeyEntityJpa(
            domain.getId().getCodCanal(),
            domain.getId().getCodProduto()
        ));
        entity.setDataFimValidade(domain.getDataFimValidade());
        entity.setDataInicioValidade(domain.getDataInicioValidade());
        entity.setStatus(domain.getStatus());
        entity.setDataManutencao(domain.getDataManutencao());
        entity.setIdUsuario(domain.getIdUsuario());
        return entity;
    }

    public static AgreementValidity toDomain(AgreementValidityEntityJpa entity) {
        if (entity == null) return null;
        AgreementValidity domain = new AgreementValidity();
        domain.setId(new AgreementValidityKey(
            entity.getId().getCodCanal(),
            entity.getId().getCodProduto()
        ));
        domain.setDataFimValidade(entity.getDataFimValidade());
        domain.setDataInicioValidade(entity.getDataInicioValidade());
        domain.setStatus(entity.getStatus());
        domain.setDataManutencao(entity.getDataManutencao());
        domain.setIdUsuario(entity.getIdUsuario());
        // Os campos canal, produto, canalProduto não são mapeados diretamente
        return domain;
    }
}