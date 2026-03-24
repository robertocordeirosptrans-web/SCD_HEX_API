package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestKeyEJpa;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;
import org.springframework.stereotype.Component;

@Component
public class HistCreditRequestMapperImpl implements HistCreditRequestMapper {
    @Override
    public HistCreditRequest toDomain(HistCreditRequestEJpa entity) {
        if (entity == null) return null;
        var domain = new HistCreditRequest();
        domain.setId(toDomainKey(entity.getId()));
        domain.setCodTipoDocumento(entity.getCodTipoDocumento());
        domain.setCodSituacao(entity.getCodSituacao());
        domain.setDtTransicao(entity.getDtTransicao());
        domain.setIdOrigemTransicao(entity.getIdOrigemTransicao());
        domain.setDtCadastro(entity.getDtCadastro());
        domain.setDtManutencao(entity.getDtManutencao());
        domain.setDtPgtoEconomica(entity.getDtPgtoEconomica());
        domain.setSqPID(entity.getSqPID());
        domain.setDtInicProcesso(entity.getDtInicProcesso());
        domain.setDtFimProcesso(entity.getDtFimProcesso());
        // domain.setIdUsuarioTransicao(...); // Ajuste conforme o domínio
        return domain;
    }

    @Override
    public HistCreditRequestEJpa toEntity(HistCreditRequest domain) {
        if (domain == null) return null;
        var entity = new HistCreditRequestEJpa();
        entity.setId(toEntityKey(domain.getId()));
        entity.setCodTipoDocumento(domain.getCodTipoDocumento());
        entity.setCodSituacao(domain.getCodSituacao());
        entity.setDtTransicao(domain.getDtTransicao());
        entity.setIdOrigemTransicao(domain.getIdOrigemTransicao());
        entity.setDtCadastro(domain.getDtCadastro());
        entity.setDtManutencao(domain.getDtManutencao());
        entity.setDtPgtoEconomica(domain.getDtPgtoEconomica());
        entity.setSqPID(domain.getSqPID());
        entity.setDtInicProcesso(domain.getDtInicProcesso());
        entity.setDtFimProcesso(domain.getDtFimProcesso());
        // entity.setIdUsuarioTransicao(...); // Ajuste conforme o domínio
        return entity;
    }

    @Override
    public HistCreditRequestKeyEJpa toEntityKey(HistCreditRequestKey key) {
        if (key == null) return null;
        var entityKey = new HistCreditRequestKeyEJpa();
        entityKey.setNumSolicitacao(key.getNumSolicitacao());
        entityKey.setCodCanal(key.getCodCanal());
        entityKey.setSeqHistSdis(key.getSeqHistSdis());
        return entityKey;
    }

    @Override
    public HistCreditRequestKey toDomainKey(HistCreditRequestKeyEJpa entityKey) {
        if (entityKey == null) return null;
        var key = new HistCreditRequestKey();
        key.setNumSolicitacao(entityKey.getNumSolicitacao());
        key.setCodCanal(entityKey.getCodCanal());
        key.setSeqHistSdis(entityKey.getSeqHistSdis());
        return key;
    }
}
