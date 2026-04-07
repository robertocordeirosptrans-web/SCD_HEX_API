package br.sptrans.scd.creditrequest.adapter.out.jpa.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestEJpaKey;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.repository.CreditRequestJpaRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CreditRequestAdapterJpa implements CreditRequestRepository {

    private final CreditRequestJpaRepository jpaRepository;

    // ── Consultas existentes ─────────────────────────────────────────

    
    @Override
    public CreditRequest save(CreditRequest cdr) {
        // Conversão do domínio para entidade JPA
        CreditRequestEJpa entity = toEntity(cdr);
        CreditRequestEJpa saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    // Conversão do domínio para entidade JPA
    private CreditRequestEJpa toEntity(CreditRequest cdr) {
        if (cdr == null) return null;
        CreditRequestEJpa entity = new CreditRequestEJpa();
        CreditRequestEJpaKey key = new CreditRequestEJpaKey();
        key.setNumSolicitacao(cdr.getNumSolicitacao());
        key.setCodCanal(cdr.getCodCanal());
        entity.setId(key);
        entity.setIdUsuarioCadastro(cdr.getIdUsuarioCadastro());
        entity.setCodTipoDocumento(cdr.getCodTipoDocumento());
        entity.setCodSituacao(cdr.getCodSituacao());
        entity.setCodFormaPagto(cdr.getCodFormaPagto());
        entity.setDtSolicitacao(cdr.getDtSolicitacao());
        entity.setDtPrevLiberacao(cdr.getDtPrevLiberacao());
        entity.setDtAceite(cdr.getDtAceite());
        entity.setDtConfirmaPagto(cdr.getDtConfirmaPagto());
        entity.setDtPagtoEconomica(cdr.getDtPagtoEconomica());
        entity.setCodUsuarioPortador(cdr.getCodUsuarioPortador());
        entity.setDtLiberacaoEfetiva(cdr.getDtLiberacaoEfetiva());
        entity.setCodEnderecoEntrega(cdr.getCodEnderecoEntrega());
        entity.setNumLote(cdr.getNumLote());
        entity.setDtFinanceira(cdr.getDtFinanceira());
        entity.setVlTotal(cdr.getVlTotal());
        entity.setDtCadastro(cdr.getDtCadastro());
        entity.setFlgCanc(cdr.getFlgCanc());
        entity.setDtManutencao(cdr.getDtManutencao());
        entity.setDtEnvioHm(cdr.getDtEnvioHm());
        entity.setIdUsuarioManutencao(cdr.getIdUsuarioManutencao());
        entity.setFlgBloq(cdr.getFlgBloq());
        // Adicione outros campos conforme necessário
        return entity;
    }

    @Override
    public Optional<CreditRequest> findByNumSolicitacaoAndCodCanal(Long numSolicitacao, String codCanal) {
        return jpaRepository.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .map(this::toDomain);
    }

    @Override
    public List<CreditRequest> findByCanalAndSituacao(String codCanal, String codSituacao) {
        return jpaRepository.findByCanalAndSituacao(codCanal, codSituacao).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByNumSolicitacao(Long numSolicitacao) {
        return jpaRepository.countByNumSolicitacao(numSolicitacao) > 0;
    }

    @Override
    public boolean existsByNumLoteAndCodCanal(String numLote, String codCanal) {
        return jpaRepository.countByNumLoteAndCodCanal(numLote, codCanal) > 0;
    }

    @Override
    public List<CreditRequest> findElegiveisParaLiberacao(String codSituacao, LocalDateTime dtInicio, LocalDateTime dtFim, int limit) {
        return jpaRepository.findElegiveisParaLiberacao(codSituacao, dtInicio, dtFim, limit).stream().map(this::toDomain).toList();
    }

    @Override
    public CreditRequest findElegiveisParaProcessamento(String codSituacao) {
        return jpaRepository.findElegiveisParaProcessamento(codSituacao).stream().findFirst().map(this::toDomain).orElse(null);
    }

    @Override
    public List<CreditRequest> findElegiveisParaConfirmacao(String codSituacao, int limit) {
        return jpaRepository.findElegiveisParaConfirmacao(codSituacao, limit).stream().map(this::toDomain).toList();
    }

    // ── Buscas específicas ─────────────────────────────────────────

    @Override
    public List<CreditRequest> findByNumSolicitacaoSpecific(Long numSolicitacao, String codCanal) {
        return jpaRepository.findByNumSolicitacaoSpecific(numSolicitacao, codCanal).stream().map(this::toDomain).toList();
    }

    @Override
    public List<CreditRequest> findByCodProduto(
            String codProduto, String codCanal,
            LocalDateTime dtInicio, LocalDateTime dtFim, int limit) {
        return jpaRepository.findByCodProduto(codProduto, codCanal, dtInicio, dtFim, limit).stream().map(this::toDomain).toList();
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
        var spec = (Specification<CreditRequestEJpa>) (root, query, cb) -> {
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
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        var page = org.springframework.data.domain.PageRequest.of(0, limit);
        return jpaRepository.findAll(spec, page).stream().map(this::toDomain).toList();
    }

    // ── Atualizações ─────────────────────────────────────────────────

    @Override
    public void update(Long numSolicitacao, String codCanal, CreditRequest cr) {
        CreditRequestEJpaKey key = new CreditRequestEJpaKey();
        key.setNumSolicitacao(numSolicitacao);
        key.setCodCanal(codCanal);
        Optional<CreditRequestEJpa> entityOpt = jpaRepository.findById(key);
        if (entityOpt.isPresent()) {
            CreditRequestEJpa entity = entityOpt.get();
            // Atualize os campos necessários
            entity.setCodSituacao(cr.getCodSituacao());
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
            .map(this::toDomain);
        }

    // ── Helpers ──────────────────────────────────────────────────────

    private CreditRequest toDomain(CreditRequestEJpa entity) {
        if (entity == null) return null;
        var cr = new CreditRequest();
        cr.setNumSolicitacao(entity.getId().getNumSolicitacao());
        cr.setCodCanal(entity.getId().getCodCanal());
        cr.setIdUsuarioCadastro(entity.getIdUsuarioCadastro());
        cr.setCodTipoDocumento(entity.getCodTipoDocumento());
        cr.setCodSituacao(entity.getCodSituacao());
        cr.setCodFormaPagto(entity.getCodFormaPagto());
        cr.setDtSolicitacao(entity.getDtSolicitacao());
        cr.setDtPrevLiberacao(entity.getDtPrevLiberacao());
        cr.setDtAceite(entity.getDtAceite());
        cr.setDtConfirmaPagto(entity.getDtConfirmaPagto());
        cr.setDtPagtoEconomica(entity.getDtPagtoEconomica());
        cr.setCodUsuarioPortador(entity.getCodUsuarioPortador());
        cr.setDtLiberacaoEfetiva(entity.getDtLiberacaoEfetiva());
        cr.setCodEnderecoEntrega(entity.getCodEnderecoEntrega());
        cr.setNumLote(entity.getNumLote());
        cr.setDtFinanceira(entity.getDtFinanceira());
        cr.setVlTotal(entity.getVlTotal());
        cr.setDtCadastro(entity.getDtCadastro());
        cr.setFlgCanc(entity.getFlgCanc());
        cr.setDtManutencao(entity.getDtManutencao());
        cr.setDtEnvioHm(entity.getDtEnvioHm());
        cr.setIdUsuarioManutencao(entity.getIdUsuarioManutencao());
        cr.setFlgBloq(entity.getFlgBloq());
        // cr.setVlPago(entity.getVlPago()); // Adicione se existir na entidade

        // Popular os itens do pedido
        if (entity.getItens() != null) {
            List<CreditRequestItems> itensDomain = entity.getItens().stream()
                .map(e -> toDomainCreditRequestItem(e))
                .toList();
            cr.setItens(itensDomain);
        }
        return cr;

    }

    // Conversão auxiliar para itens
    private CreditRequestItems toDomainCreditRequestItem(CreditRequestItemsEJpa e) {
        if (e == null) return null;
        CreditRequestItems item = new CreditRequestItems();
        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(e.getId().getNumSolicitacao());
        key.setNumSolicitacaoItem(e.getId().getNumSolicitacaoItem());
        key.setCodCanal(e.getId().getCodCanal());
        item.setId(key);
        item.setCodCanal(e.getCodCanal());
        item.setIdUsuarioCadastro(e.getIdUsuarioCadastro());
        item.setCodVersao(e.getCodVersao());
        item.setNumLogicoCartao(e.getNumLogicoCartao());
        item.setCodProduto(e.getCodProduto());
        item.setCodTipoDocumento(e.getCodTipoDocumento());
        item.setCodSituacao(e.getCodSituacao());
        item.setQtdItem(e.getQtdItem());
        item.setVlUnitario(e.getVlUnitario());
        item.setVlItem(e.getVlItem());
        item.setDtRecarga(e.getDtRecarga());
        item.setVlCarregado(e.getVlCarregado());
        item.setVlAjuste(e.getVlAjuste());
        item.setFlgAjuste(e.getFlgAjuste());
        item.setIdFuncionario(e.getIdFuncionario());
        // item.setCodAssinaturaHsm(e.getCodAssinaturaHsm());
        item.setDtCadastro(e.getDtCadastro());
        item.setDtManutencao(e.getDtManutencao());
        item.setSeqRecarga(e.getSeqRecarga());
        item.setDtEnvioHm(e.getDtEnvioHm());
        item.setDtRetornoHm(e.getDtRetornoHm());
        item.setIdUsuarioManutencao(e.getIdUsuarioManutencao());
        item.setDtAssinatura(e.getDtAssinatura());
        item.setDtPagtoEconomica(e.getDtPagtoEconomica());
        item.setSqPid(e.getSqPid());
        item.setDtInicProcesso(e.getDtInicProcesso());
        item.setIdUsuarioCartao(e.getIdUsuarioCartao());
        // Adicione outros campos conforme necessário
        return item;
    }

}
