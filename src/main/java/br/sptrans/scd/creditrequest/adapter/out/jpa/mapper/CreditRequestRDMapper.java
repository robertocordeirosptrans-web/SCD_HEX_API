package br.sptrans.scd.creditrequest.adapter.out.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestRDEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestRDEntityKey;
import br.sptrans.scd.creditrequest.domain.CreditRequestRD;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditRequestRDMapper {

    default CreditRequestRDEntity toEntity(CreditRequestRD domain) {
        if (domain == null) return null;
        CreditRequestRDEntityKey key = new CreditRequestRDEntityKey(
                domain.getCodCanal(),
                domain.getNumSolicitacao(),
                domain.getCodCanalDistribuicao());
        CreditRequestRDEntity entity = new CreditRequestRDEntity();
        entity.setId(key);
        entity.setIdUsuarioCadastro(domain.getIdUsuarioCadastro());
        entity.setIdUsuarioManutencao(domain.getIdUsuarioManutencao());
        entity.setDtCadastro(domain.getDtCadastro());
        entity.setDtManutencao(domain.getDtManutencao());
        return entity;
    }

    default CreditRequestRD toDomain(CreditRequestRDEntity entity) {
        if (entity == null) return null;
        CreditRequestRD domain = new CreditRequestRD();
        domain.setNumSolicitacao(entity.getId().getNumSolicitacao());
        domain.setCodCanal(entity.getId().getCodCanal());
        domain.setCodCanalDistribuicao(entity.getId().getCodCanalDistribuicao());
        domain.setIdUsuarioCadastro(entity.getIdUsuarioCadastro());
        domain.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        domain.setDtCadastro(entity.getDtCadastro());
        domain.setDtManutencao(entity.getDtManutencao());
        return domain;
    }
}
