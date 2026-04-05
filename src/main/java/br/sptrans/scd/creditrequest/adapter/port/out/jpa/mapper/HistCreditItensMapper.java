package br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.HistCreditRequestItemsKeyEJpa;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItemsKey;

public class HistCreditItensMapper {
    public static HistCreditRequestItems toDomain(HistCreditRequestItemsEJpa entity) {
        if (entity == null) return null;
        HistCreditRequestItems domain = new HistCreditRequestItems();
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
            User user = new User();
            user.setIdUsuario(entity.getIdUsuarioTransicao());
            domain.setIdUsuarioTransicao(user);
        }
        return domain;
    }

    public static HistCreditRequestItemsEJpa toEntity(HistCreditRequestItems domain) {
        if (domain == null) return null;
        HistCreditRequestItemsEJpa entity = new HistCreditRequestItemsEJpa();
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

    public static HistCreditRequestItemsKey toDomainKey(HistCreditRequestItemsKeyEJpa entityKey) {
        if (entityKey == null) return null;
        HistCreditRequestItemsKey key = new HistCreditRequestItemsKey();
        key.setNumSolicitacao(entityKey.getNumSolicitacao());
        key.setNumSolicitacaoItem(entityKey.getNumSolicitacaoItem());
        key.setCodCanal(entityKey.getCodCanal());
        key.setSeqHistSdis(entityKey.getSeqHistSdis());
        return key;
    }

    public static HistCreditRequestItemsKeyEJpa toEntityKey(HistCreditRequestItemsKey domainKey) {
        if (domainKey == null) return null;
        HistCreditRequestItemsKeyEJpa key = new HistCreditRequestItemsKeyEJpa();
        key.setNumSolicitacao(domainKey.getNumSolicitacao());
        key.setNumSolicitacaoItem(domainKey.getNumSolicitacaoItem());
        key.setCodCanal(domainKey.getCodCanal());
        key.setSeqHistSdis(domainKey.getSeqHistSdis());
        return key;
    }
}
