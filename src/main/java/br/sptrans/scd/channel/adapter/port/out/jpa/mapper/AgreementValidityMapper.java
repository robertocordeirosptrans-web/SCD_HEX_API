package br.sptrans.scd.channel.adapter.port.out.jpa.mapper;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AgreementValidityEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AgreementValidityKeyEntityJpa;
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
        entity.setDtFimValidade(domain.getDtFimValidade());
        entity.setDtInicioValidade(domain.getDtInicioValidade());
        entity.setCodStatus(domain.getCodStatus());
        entity.setDtManutencao(domain.getDtManutencao());
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
        domain.setDtFimValidade(entity.getDtFimValidade());
        domain.setDtInicioValidade(entity.getDtInicioValidade());
        domain.setCodStatus(entity.getCodStatus());
        domain.setDtManutencao(entity.getDtManutencao());
        domain.setIdUsuario(entity.getIdUsuario());
        // Os campos canal, produto, canalProduto não são mapeados diretamente
        return domain;
    }
}