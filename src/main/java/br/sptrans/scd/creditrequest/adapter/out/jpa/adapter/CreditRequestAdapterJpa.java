package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestEntityKey;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.CreditRequestJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestAdapterJpa implements CreditRequestPort {

    private final CreditRequestJpaRepository jpaRepository;
    private final CreditRequestMapper mapper;

    // ── Consultas existentes ─────────────────────────────────────────

    
    @Override
    public CreditRequest save(CreditRequest cdr) {
        CreditRequestEntity entity = mapper.toJpaEntity(cdr);
        CreditRequestEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        return jpaRepository.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .map(mapper::toDomain);
    }

    @Override
    public List<CreditRequest> findByCanalAndSituacao(String codCanal, String codSituacao) {
        return jpaRepository.findByCanalAndSituacao(codCanal, codSituacao).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByNumSolicitacao(Long numSolicitacao) {
        return jpaRepository.countByNumSolicitacao(numSolicitacao) > 0;
    }

    @Override
    public boolean existsByNumLoteAndCodCanal(String numLote, String codCanal) {
        return jpaRepository.countByNumLoteAndCodCanal(numLote, codCanal) > 0;
    }





    // ── Buscas específicas ─────────────────────────────────────────

    @Override
    public List<CreditRequest> findByNumSolicitacaoSpecific(Long numSolicitacao, String codCanal) {
        return jpaRepository.findByNumSolicitacaoSpecific(numSolicitacao, codCanal).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CreditRequest> findByCodProduto(
            String codProduto, String codCanal,
            LocalDateTime dtInicio, LocalDateTime dtFim, int limit) {
        return jpaRepository.findByCodProduto(codProduto, codCanal, dtInicio, dtFim, limit).stream().map(mapper::toDomain).toList();
    }

    // ── Busca paginada por cursor ────────────────────────────────────

    @Override
    public List<CreditRequest> findWithCursor(
            Long cursorNumSolicitacao,
            String cursorCodCanal,
            String codCanal,
            String codSituacao,
            String numLote,
            String codFormaPagto,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            LocalDateTime dtLiberacaoEfetivaInicio,
            LocalDateTime dtLiberacaoEfetivaFim,
            LocalDateTime dtPagtoEconomicaInicio,
            LocalDateTime dtPagtoEconomicaFim,
            LocalDateTime dtFinanceiraInicio,
            LocalDateTime dtFinanceiraFim,
            LocalDateTime dtAlteracaoInicio,
            LocalDateTime dtAlteracaoFim,
            BigDecimal vlTotalMin,
            BigDecimal vlTotalMax,
            int limit) {
        var spec = (Specification<CreditRequestEntity>) (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (cursorNumSolicitacao != null) {
                var p1 = cb.lessThan(root.get("id").get("numSolicitacao"), cursorNumSolicitacao);
                var p2 = cb.and(
                        cb.equal(root.get("id").get("numSolicitacao"), cursorNumSolicitacao),
                        cb.greaterThan(root.get("id").get("codCanal"), cursorCodCanal)
                );
                predicates.add(cb.or(p1, p2));
            }
            if (codCanal != null) predicates.add(cb.equal(root.get("id").get("codCanal"), codCanal));
            if (codSituacao != null) predicates.add(cb.equal(root.get("codSituacao"), codSituacao));
            if (numLote != null) predicates.add(cb.equal(root.get("numLote"), numLote));
            if (codFormaPagto != null) predicates.add(cb.equal(root.get("codFormaPagto"), codFormaPagto));
            if (dtInicio != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dtSolicitacao"), dtInicio));
            if (dtFim != null) predicates.add(cb.lessThanOrEqualTo(root.get("dtSolicitacao"), dtFim));
            if (dtLiberacaoEfetivaInicio != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dtLiberacaoEfetiva"), dtLiberacaoEfetivaInicio));
            if (dtLiberacaoEfetivaFim != null) predicates.add(cb.lessThanOrEqualTo(root.get("dtLiberacaoEfetiva"), dtLiberacaoEfetivaFim));
            if (dtPagtoEconomicaInicio != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dtPagtoEconomica"), dtPagtoEconomicaInicio));
            if (dtPagtoEconomicaFim != null) predicates.add(cb.lessThanOrEqualTo(root.get("dtPagtoEconomica"), dtPagtoEconomicaFim));
            if (dtFinanceiraInicio != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dtFinanceira"), dtFinanceiraInicio));
            if (dtFinanceiraFim != null) predicates.add(cb.lessThanOrEqualTo(root.get("dtFinanceira"), dtFinanceiraFim));
            if (dtAlteracaoInicio != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dtManutencao"), dtAlteracaoInicio));
            if (dtAlteracaoFim != null) predicates.add(cb.lessThanOrEqualTo(root.get("dtManutencao"), dtAlteracaoFim));
            if (vlTotalMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("vlTotal"), vlTotalMin));
            if (vlTotalMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("vlTotal"), vlTotalMax));
            query.orderBy(cb.desc(root.get("id").get("numSolicitacao")), cb.asc(root.get("id").get("codCanal")));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        var page = org.springframework.data.domain.PageRequest.of(0, limit);
        return jpaRepository.findAll(spec, page).stream().map(mapper::toDomain).toList();
    }

    // ── Atualizações ─────────────────────────────────────────────────

    @Override
    public void update(Long numSolicitacao, String codCanal, CreditRequest cr) {
        CreditRequestEntityKey key = new CreditRequestEntityKey();
        key.setNumSolicitacao(numSolicitacao);
        key.setCodCanal(codCanal);
        Optional<CreditRequestEntity> entityOpt = jpaRepository.findById(key);
        if (entityOpt.isPresent()) {
            CreditRequestEntity entity = entityOpt.get();
            // Atualize os campos necessários
            entity.setCodSituacao(cr.getCodSituacao().getCode());
            entity.setCodFormaPagto(cr.getCodFormaPagto());
            entity.setDtAceite(cr.getDtAceite());
            entity.setDtConfirmaPagto(cr.getDtConfirmaPagto());
            entity.setDtPagtoEconomica(cr.getDtPagtoEconomica());
            entity.setDtLiberacaoEfetiva(cr.getDtLiberacaoEfetiva());
            // entity.setVlPago(cr.getVlPago()); // Adicione se existir na entidade
            entity.setFlgCanc(cr.getFlgCanc());
            entity.setFlgBloq(cr.getFlgBloq());
            entity.setNumLote(cr.getNumLote());
            entity.setDtManutencao(LocalDateTime.now());
            jpaRepository.save(entity);
        }
    }

    @Override
        public Optional<CreditRequest> findByCodTipoDocumentoAndIdUsuarioCadastro(
            String codTipoDocumento, Long idUsuarioCadastro) {
        return jpaRepository.findByCodTipoDocumentoAndIdUsuarioCadastro(codTipoDocumento, idUsuarioCadastro)
            .map(mapper::toDomain);
        }

    @Override
    public Optional<String> findCodCanalDistribuicao(Long numSolicitacao, String codCanal) {
        return jpaRepository.findCodCanalDistribuicao(numSolicitacao, codCanal);
    }

}
