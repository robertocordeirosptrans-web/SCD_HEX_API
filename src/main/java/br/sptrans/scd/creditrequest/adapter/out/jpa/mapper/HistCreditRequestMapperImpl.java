package br.sptrans.scd.creditrequest.adapter.out.jpa.mapper;

import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.HistCreditRequestEntityKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestKey;

@Component
public class HistCreditRequestMapperImpl implements HistCreditRequestMapper {
    @Override
    public HistCreditRequest toDomain(HistCreditRequestEntity entity) {
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
        
        if (entity.getIdUsuarioTransicao() != null) {
            var user = new User();
            user.setIdUsuario(entity.getIdUsuarioTransicao());
            domain.setIdUsuarioTransicao(user);
        }
        return domain;
    }

    @Override
    public HistCreditRequestEntity toEntity(HistCreditRequest domain) {
        if (domain == null) return null;
        var entity = new HistCreditRequestEntity();
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
        if (domain.getIdUsuarioTransicao() != null) {
            entity.setIdUsuarioTransicao(domain.getIdUsuarioTransicao().getIdUsuario());
        }
        return entity;
    }

    @Override
    public HistCreditRequestEntityKey toEntityKey(HistCreditRequestKey key) {
        if (key == null) return null;
        var entityKey = new HistCreditRequestEntityKey();
        entityKey.setNumSolicitacao(key.getNumSolicitacao());
        entityKey.setCodCanal(key.getCodCanal());
        entityKey.setSeqHistSdis(key.getSeqHistSdis());
        return entityKey;
    }

    @Override
    public HistCreditRequestKey toDomainKey(HistCreditRequestEntityKey entityKey) {
        if (entityKey == null) return null;
        var key = new HistCreditRequestKey();
        key.setNumSolicitacao(entityKey.getNumSolicitacao());
        key.setCodCanal(entityKey.getCodCanal());
        key.setSeqHistSdis(entityKey.getSeqHistSdis());
        return key;
    }
}
